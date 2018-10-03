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
import se.addq.notifysales.cinode.model.ProjectResponse;
import se.addq.notifysales.cinode.model.TokenResponse;
import se.addq.notifysales.configuration.H2TestProfileJpaConfig;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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

    private static boolean firstRun = true;

    @Before
    public void setUp() throws JsonProcessingException {
        mockServer = MockRestServiceServer.bindTo(restTemplate).bufferContent().build();
        if (firstRun) {
            TokenResponse tokenResponse = getTokenResponse();
            String tokenListJson = objectMapper.writeValueAsString(tokenResponse);
            mockServer.expect(manyTimes(), requestTo("https://dummy/token")).andExpect(method(HttpMethod.GET))
                    .andRespond(withSuccess(tokenListJson, MediaType.APPLICATION_JSON));
            firstRun = false;
        }
    }

    @Test()
    public void requestedCompaniesResponseWillContainCompanyName() throws JsonProcessingException {
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

    @Test()
    public void requestedProjectResponseWillContainAssignment() {
        mockServer.expect(manyTimes(), requestTo("https://dummy/v0.1/companies/109/projects/1")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(getProjectJsonResponse(), MediaType.APPLICATION_JSON));

        ProjectResponse projectResponse = this.cinodeImpl.getProject(1);
        mockServer.verify();
        assertThat(projectResponse.getAssignmentResponses().size()).isOne();
        mockServer.reset();
    }

    @Test()
    public void requestedProjectResponseWillContainCustomer() {
        mockServer.expect(manyTimes(), requestTo("https://dummy/v0.1/companies/109/projects/1")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(getProjectJsonResponse(), MediaType.APPLICATION_JSON));

        ProjectResponse projectResponse = this.cinodeImpl.getProject(1);
        mockServer.verify();
        assertThat(projectResponse.getCustomer()).isNotNull();
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

    private String getProjectJsonResponse() {
        return "{\"company\":{\"id\":109,\"name\":\"AddQ\",\"seoId\":\"addq\",\"description\":\"Vi erbjuder specialistkonsulter och\\r\\nutbildning inom test och testledning, utveckling av test- och mätsystem samt effektivisering.\",\"links\":[{\"href\":\"/v0.1/companies/109\",\"rel\":\"self\",\"methods\":[\"GET\"]},{\"href\":\"/v0.1/companies/109/teams\",\"rel\":\"teams\",\"methods\":[\"GET\"]},{\"href\":\"/v0.1/companies/109/users\",\"rel\":\"users\",\"methods\":[\"GET\"]},{\"href\":\"/v0.1/companies/109/customers\",\"rel\":\"customers\",\"methods\":[\"GET\"]}]},\"customer\":{\"id\":17013,\"companyId\":109,\"name\":\"Test Company\",\"description\":null,\"identification\":null,\"seoId\":\"17013-test\",\"status\":1,\"links\":[{\"href\":\"/v0.1/companies/109/customers/17013\",\"rel\":\"self\",\"methods\":[\"GET\",\"PUT\",\"POST\",\"DELETE\"]}]},\"seoId\":\"ersattare-mera-ux\",\"locationId\":null,\"probability\":null,\"estimatedValue\":null,\"estimatedCloseDate\":null,\"identifier\":null,\"customerIdentifier\":null,\"managers\":[],\"salesManager\":{\"userId\":\"bfeeea28-471b-40c3-88f5-2962467719c8\",\"companyId\":109,\"id\":445,\"seoId\":\"445-michael-albrecht\",\"firstName\":\"Michael\",\"lastName\":\"Albrecht\",\"companyUserType\":0,\"status\":3,\"links\":null},\"intermediator\":null,\"events\":[],\"customerContacts\":[],\"intermediatorContacts\":[],\"assignments\":[{\"companyId\":109,\"customerId\":17013,\"projectId\":27926,\"id\":29138,\"title\":\"Ersättare Reda men mera UX\",\"description\":null,\"startDate\":\"2018-10-08T00:00:00\",\"endDate\":\"2018-10-08T00:00:00\",\"links\":null}],\"tags\":[],\"pipelineId\":240,\"currentStageId\":360,\"currency\":{\"id\":1,\"currencyCode\":\"SEK\",\"description\":\"Svenska kronor\"},\"projectReferences\":[],\"companyId\":109,\"customerId\":17013,\"id\":27926,\"title\":\"Ersättare Reda men mera UX\",\"description\":null,\"links\":[{\"href\":\"/v0.1/companies/109/projects/27926\",\"rel\":\"self\",\"methods\":[\"GET\",\"PUT\",\"POST\",\"DELETE\"]}]}";
    }
}