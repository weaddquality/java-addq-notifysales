package se.addq.notifysales.integrationtest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import se.addq.notifysales.Application;
import se.addq.notifysales.cinode.CinodeImpl;
import se.addq.notifysales.cinode.model.CompaniesResponse;
import se.addq.notifysales.cinode.model.ProjectList;
import se.addq.notifysales.cinode.model.TokenResponse;
import se.addq.notifysales.configuration.H2TestProfileJpaConfig;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.client.ExpectedCount.manyTimes;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {
        Application.class, H2TestProfileJpaConfig.class})
@ActiveProfiles("test")
public class CinodeClientTest {

    @Value("${cinode.baseurl}")
    private String baseUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CinodeImpl cinodeImpl;

    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper objectMapper;

    private TokenResponse tokenResponse = new TokenResponse();

    @Before
    public void setUp() {
        mockServer = MockRestServiceServer.bindTo(restTemplate).bufferContent().build();
    }

    @Test()
    public void requestedCompaniesResponseWillContainCompanyName() throws JsonProcessingException {
        TokenResponse tokenResponse = getTokenResponse();
        String tokenListJson = objectMapper.writeValueAsString(tokenResponse);
        mockServer.expect(manyTimes(), requestTo("https://dummy/token")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(tokenListJson, MediaType.APPLICATION_JSON));

        List<CompaniesResponse> companiesResponseList = getCompaniesResponses();
        String companyListJson = objectMapper.writeValueAsString(companiesResponseList);
        mockServer.expect(manyTimes(), requestTo("https://dummy/v0.1/companies")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(companyListJson, MediaType.APPLICATION_JSON));

        List<CompaniesResponse> details = this.cinodeImpl.getCompanies();
        mockServer.verify();
        assertEquals(getCompaniesResponses().get(0).getName(), details.get(0).getName());
        mockServer.reset();
    }

    @Test()
    public void requestedProjectsResponseWillContainCompanyId() throws JsonProcessingException {
        List<ProjectList> projectListResponse = getProjectResponses();
        String projectsListJson = objectMapper.writeValueAsString(projectListResponse);
        mockServer.expect(manyTimes(), requestTo("https://dummy/v0.1/companies/109/projects")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(projectsListJson, MediaType.APPLICATION_JSON));

        List<ProjectList> projectLists = this.cinodeImpl.getProjects();
        mockServer.verify();
        assertEquals(getProjectResponses().get(0).getCompanyId(), projectLists.get(0).getCompanyId());
        mockServer.reset();
    }

    private TokenResponse getTokenResponse() {
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setRefreshToken("token");
        tokenResponse.setAccessToken("access");
        return tokenResponse;
    }

    private List<CompaniesResponse> getCompaniesResponses() {
        CompaniesResponse companiesResponse = new CompaniesResponse();
        companiesResponse.setName("Nisse");
        List<CompaniesResponse> companiesResponseList = new ArrayList<>();
        companiesResponseList.add(companiesResponse);
        return companiesResponseList;
    }

    private List<ProjectList> getProjectResponses() {
        ProjectList projectList = new ProjectList();
        projectList.setCompanyId(123);
        List<ProjectList> projectListList = new ArrayList<>();
        projectListList.add(projectList);
        return projectListList;
    }
}