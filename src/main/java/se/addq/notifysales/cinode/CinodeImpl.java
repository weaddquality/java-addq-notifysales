package se.addq.notifysales.cinode;

import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import se.addq.notifysales.cinode.model.*;
import se.addq.notifysales.utils.SleepUtil;

import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CinodeImpl implements CinodeApi {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int TOKEN_TIMEOUT_MS = 1000 * 60 * 5;

    @Value("${cinode.baseurl}")
    private String baseUrl;
    @Value("${cinode.password}")
    private String password;
    @Value("${cinode.user}")
    private String user;

    @Value("${cinode.addq.company.id}")
    private int addQCompanyId;
    @Value("${cinode.request.interval.ms}")
    private int cinodeRequestIntervalInMilliSeconds;

    private final static String API_VERSION = "v0.1";

    private static TokenResponse tokenResponse;

    private LocalDateTime dateTimeTokenReceived;

    private final RestTemplate restTemplate;

    @Autowired
    CinodeImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public TokenResponse getToken() {
        HttpEntity<String> entity = new HttpEntity<>(getBasicAuthHeaders());
        if (isTokenRefreshNeeded()) {
            ResponseEntity<TokenResponse> tokenResponseResponseEntity = restTemplate.exchange(baseUrl + "/token", HttpMethod.GET, entity, TokenResponse.class);
            if (tokenResponseResponseEntity.getStatusCodeValue() != 200) {
                log.error("Did not return OK response code -> {}", tokenResponseResponseEntity.getStatusCode());
                return tokenResponse;
            }
            tokenResponse = tokenResponseResponseEntity.getBody();
            dateTimeTokenReceived = LocalDateTime.now();
            log.info("Token was re-newed");
        }
        return tokenResponse;
    }

    private boolean isTokenRefreshNeeded() {
        if (tokenResponse == null || dateTimeTokenReceived == null) {
            dateTimeTokenReceived = LocalDateTime.now();
            return true;
        }
        if ((LocalDateTime.now().isAfter(dateTimeTokenReceived.plusMinutes(TOKEN_TIMEOUT_MS / (1000 * 60))))) {
            return true;
        }
        Duration dur = Duration.between(dateTimeTokenReceived, LocalDateTime.now());
        log.debug("Time left to renew token {} ms ", TOKEN_TIMEOUT_MS - dur.toMillis());
        return false;
    }

    @Override
    public List<CompaniesResponse> getCompanies() {
        HttpEntity<String> entity = new HttpEntity<>(getHttpHeadersWithToken());
        ResponseEntity<List<CompaniesResponse>> companiesResponse = restTemplate.exchange(getBaseUrlWithVersion() + "/companies", HttpMethod.GET, entity, new ParameterizedTypeReference<List<CompaniesResponse>>() {
        });
        log.info("Response: {}", companiesResponse);
        if (companiesResponse.getStatusCodeValue() != 200) {
            log.error("Did not return OK response code -> {}", companiesResponse.getStatusCode());
            return new ArrayList<>();
        }
        return companiesResponse.getBody();
    }

    @Override
    public List<ProjectList> getProjects() {
        HttpEntity<String> entity = new HttpEntity<>(getHttpHeadersWithToken());
        ResponseEntity<List<ProjectList>> listResponseEntity = restTemplate.exchange(getBaseUrlForCompany() + "/projects", HttpMethod.GET, entity, new ParameterizedTypeReference<List<ProjectList>>() {
        });
        if (listResponseEntity.getStatusCodeValue() != 200) {
            log.error("Did not return OK response code -> {}", listResponseEntity.getStatusCode());
            return new ArrayList<>();
        }
        log.debug("Response: {}", listResponseEntity);
        return listResponseEntity.getBody();
    }

    @Override
    public ProjectResponse getProject(int id) {
        SleepUtil.sleepMilliSeconds(cinodeRequestIntervalInMilliSeconds);
        HttpEntity<String> entity = new HttpEntity<>(getHttpHeadersWithToken());
        ResponseEntity<ProjectResponse> projectResponseResponseEntity = restTemplate.exchange(getBaseUrlForCompany() + "/projects/" + id, HttpMethod.GET, entity, new ParameterizedTypeReference<ProjectResponse>() {
        });
        if (projectResponseResponseEntity.getStatusCodeValue() != 200) {
            log.error("Did not return OK response code -> {}", projectResponseResponseEntity.getStatusCode());
            return new ProjectResponse();
        }
        log.debug("Response: {}", projectResponseResponseEntity);
        return projectResponseResponseEntity.getBody();
    }

    @Override
    public ProjectAssignmentResponse getProjectAssignment(int projectId, int assignmentId) {
        SleepUtil.sleepMilliSeconds(cinodeRequestIntervalInMilliSeconds);
        HttpEntity<String> entity = new HttpEntity<>(getHttpHeadersWithToken());
        ResponseEntity<ProjectAssignmentResponse> assignmentResponseResponseEntity = restTemplate.exchange(getBaseUrlForCompany() + "/projects/" + projectId + "/projectassignments/" + assignmentId, HttpMethod.GET, entity, new ParameterizedTypeReference<ProjectAssignmentResponse>() {
        });
        if (assignmentResponseResponseEntity.getStatusCodeValue() != 200) {
            log.error("Did not return OK response code -> {}", assignmentResponseResponseEntity.getStatusCode());
            return new ProjectAssignmentResponse();
        }
        log.debug("Response: {}", assignmentResponseResponseEntity);
        return assignmentResponseResponseEntity.getBody();
    }

    @Override
    public List<Team> getTeamsForUser(int userId) {
        SleepUtil.sleepMilliSeconds(cinodeRequestIntervalInMilliSeconds);
        HttpEntity<String> entity = new HttpEntity<>(getHttpHeadersWithToken());
        ResponseEntity<List<Team>> listResponseEntity = restTemplate.exchange(getBaseUrlForCompany() + "/users/" + userId + "/teams/", HttpMethod.GET, entity, new ParameterizedTypeReference<List<Team>>() {
        });
        if (listResponseEntity.getStatusCodeValue() != 200) {
            log.error("Did not return OK response code -> {}", listResponseEntity.getStatusCode());
            return new ArrayList<>();
        }
        log.debug("Response: {}", listResponseEntity);
        return listResponseEntity.getBody();
    }

    @Override
    public List<Team> getTeamsForCompany() {
        HttpEntity<String> entity = new HttpEntity<>(getHttpHeadersWithToken());
        ResponseEntity<List<Team>> listResponseEntity = restTemplate.exchange(getBaseUrlForCompany() + "/teams/", HttpMethod.GET, entity, new ParameterizedTypeReference<List<Team>>() {
        });
        if (listResponseEntity.getStatusCodeValue() != 200) {
            log.error("Did not return OK response code -> {}", listResponseEntity.getStatusCode());
            return new ArrayList<>();
        }
        log.debug("Response: {}", listResponseEntity);
        return listResponseEntity.getBody();
    }

    private HttpHeaders getBasicAuthHeaders() {
        String plainCreds = String.format("%s:%s", user, password);
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);
        return headers;
    }

    private HttpHeaders getHttpHeadersWithToken() {
        getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + tokenResponse.getAccessToken());
        return headers;
    }

    public String getBaseUrlForCompany() {
        return getBaseUrlWithVersion() + "/companies/" + addQCompanyId;
    }

    private String getBaseUrlWithVersion() {
        String url = baseUrl + "/" + API_VERSION;
        log.debug("Request Endpoint Base URL with version: {}", url);
        return url;
    }


}
