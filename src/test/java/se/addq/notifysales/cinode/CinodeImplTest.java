package se.addq.notifysales.cinode;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import se.addq.notifysales.cinode.model.*;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class CinodeImplTest {

    private CinodeApi cinodeApi;

    @Mock
    RestTemplate restTemplateMock;

    @Before
    public void setup() {
        this.cinodeApi = new CinodeImpl(restTemplateMock);
        setExternalProperties();
        setMockitoTokenResponse();
    }

    private void setExternalProperties() {
        ReflectionTestUtils.setField(cinodeApi, "password", "password");
        ReflectionTestUtils.setField(cinodeApi, "user", "user");
        ReflectionTestUtils.setField(cinodeApi, "baseUrl", "http://dummy");
    }


    @Test
    public void getTokenShallContainAccessToken() {
        String accessToken = cinodeApi.getToken().getAccessToken();
        assertThat(accessToken).isEqualTo("token");
    }

    @Test
    public void getTokenShallContainRefreshToken() {
        String refreshToken = cinodeApi.getToken().getRefreshToken();
        assertThat(refreshToken).isEqualTo("refresh");
    }

    @Test
    public void getProjectsShallReturnListOfProjects() {
        List<ProjectList> projectListList = new ArrayList<>();
        projectListList.add(new ProjectList(1));
        setMockitoGetProjectsResponse(projectListList);
        List<ProjectList> projectsRespons = cinodeApi.getProjects();
        assertThat(projectsRespons.size()).isEqualTo(1);
    }

    @Test
    public void getCompaniesShallReturnListOfCompanies() {
        List<CompaniesResponse> companiesResponses = new ArrayList<>();
        CompaniesResponse companiesResponse = new CompaniesResponse();
        LinksResponse linksResponse = new LinksResponse();
        linksResponse.setHref("http://dummy");
        linksResponse.setRel("self");
        List<LinksResponse> linksResponses = new ArrayList<>();
        linksResponses.add(linksResponse);
        companiesResponse.setLinks(linksResponses);
        companiesResponses.add(companiesResponse);
        setMockitoGetCompaniesResponse(companiesResponses);
        List<CompaniesResponse> companiesResponseList = cinodeApi.getCompanies();
        assertThat(companiesResponseList.size()).isEqualTo(1);
    }


    @Test
    public void getTeamsForCompanyShallReturnListOfTeams() {
        List<Team> teamListResponse = new ArrayList<>();
        teamListResponse.add(new Team());
        setMockitoGetTeamsForCompanyResponse(teamListResponse);
        List<Team> teamList = cinodeApi.getTeamsForCompany();
        assertThat(teamList.size()).isEqualTo(1);
    }


    @Test
    public void getTeamsForUserIdShallReturnListOfTeamsForSpecificUser() {
        List<Team> teamListResponse = new ArrayList<>();
        teamListResponse.add(new Team());
        setMockitoGetTeamsForUserResponse(teamListResponse);
        List<Team> teamList = cinodeApi.getTeamsForUser(1);
        assertThat(teamList.size()).isEqualTo(1);
    }


    @Test
    public void getProjectShallReturnProjectResponseWithCustomer() {
        String customerName = "IT-bolaget";
        ProjectResponse response = new ProjectResponse();
        response.setCustomer(new Customer(customerName));
        setMockitoProjectResponse(response);
        ProjectResponse projectResponse = cinodeApi.getProject(1);
        assertThat(projectResponse.getCustomer().getName()).isEqualTo(customerName);
    }


    @Test
    public void getProjectShallReturnProjectResponseWithListOfAssignments() {
        ProjectResponse response = new ProjectResponse();
        List<Assignment> assignmentList = new ArrayList<>();
        assignmentList.add(new Assignment());
        response.setAssignments(assignmentList);
        setMockitoProjectResponse(response);
        ProjectResponse projectResponse = cinodeApi.getProject(1);
        assertThat(projectResponse.getAssignments().size()).isNotZero();
    }


    @Test
    public void getProjectAssignmentShallReturnProjectAssignmentsResponseWithAssigned() {
        ProjectAssignmentResponse response = new ProjectAssignmentResponse();
        Assigned assigned = new Assigned();
        assigned.setFirstName("Nisse");
        assigned.setLastName("Hult");
        response.setAssigned(assigned);
        setMockitoProjectAssignmentResponse(response);
        ProjectAssignmentResponse projectAssignmentResponse = cinodeApi.getProjectAssignment(1, 1);
        assertThat(projectAssignmentResponse.getAssigned().getFirstName()).isEqualTo("Nisse");
        assertThat(projectAssignmentResponse.getAssigned().getLastName()).isEqualTo("Hult");
    }


    @Test
    public void getProjectAssignmentShallReturnProjectAssignmentsResponseWithCustomer() {
        String customerName = "IT-bolaget";
        ProjectAssignmentResponse response = new ProjectAssignmentResponse();
        response.setCustomer(new Customer(customerName));
        setMockitoProjectAssignmentResponse(response);
        ProjectAssignmentResponse projectAssignmentResponse = cinodeApi.getProjectAssignment(1, 1);
        assertThat(projectAssignmentResponse.getCustomer().getName()).isEqualTo(customerName);
    }


    private void setMockitoTokenResponse() {
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken("token");
        tokenResponse.setRefreshToken("refresh");
        ResponseEntity<TokenResponse> responseResponseEntity = new ResponseEntity<>(tokenResponse, HttpStatus.OK);
        Mockito.when(restTemplateMock.exchange(
                Mockito.eq(ReflectionTestUtils.getField(cinodeApi, "baseUrl") + "/token"),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(HttpEntity.class),
                Mockito.eq(TokenResponse.class))).thenReturn(responseResponseEntity);
    }


    private void setMockitoGetProjectsResponse(List<ProjectList> projectListList) {
        ResponseEntity<List<ProjectList>> responseResponseEntity2 = new ResponseEntity<>(projectListList, HttpStatus.OK);
        Mockito.when(restTemplateMock.exchange(
                Mockito.eq(cinodeApi.getBaseUrlForCompany() + "/projects"),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(HttpEntity.class),
                ArgumentMatchers.<ParameterizedTypeReference<List<ProjectList>>>any())).thenReturn(responseResponseEntity2);
    }

    private void setMockitoProjectResponse(ProjectResponse projectResponse) {
        {
            ResponseEntity<ProjectResponse> responseEntity = new ResponseEntity<>(projectResponse, HttpStatus.OK);
            Mockito.when(restTemplateMock.exchange(
                    Mockito.eq(cinodeApi.getBaseUrlForCompany() + "/projects/" + 1),
                    Mockito.eq(HttpMethod.GET),
                    Mockito.any(HttpEntity.class),
                    ArgumentMatchers.<ParameterizedTypeReference<ProjectResponse>>any())).thenReturn(responseEntity);
        }
    }

    private void setMockitoProjectAssignmentResponse(ProjectAssignmentResponse response) {
        {
            ResponseEntity<ProjectAssignmentResponse> responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
            Mockito.when(restTemplateMock.exchange(
                    Mockito.eq(cinodeApi.getBaseUrlForCompany() + "/projects/" + 1 + "/projectassignments/" + 1),
                    Mockito.eq(HttpMethod.GET),
                    Mockito.any(HttpEntity.class),
                    ArgumentMatchers.<ParameterizedTypeReference<ProjectAssignmentResponse>>any())).thenReturn(responseEntity);
        }
    }

    private void setMockitoGetCompaniesResponse(List<CompaniesResponse> companiesResponses) {
        ResponseEntity<List<CompaniesResponse>> listResponseEntity = new ResponseEntity<>(companiesResponses, HttpStatus.OK);
        Mockito.when(restTemplateMock.exchange(
                Mockito.eq(ReflectionTestUtils.getField(cinodeApi, "baseUrl") + "/v0.1" + "/companies"),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(HttpEntity.class),
                ArgumentMatchers.<ParameterizedTypeReference<List<CompaniesResponse>>>any())).thenReturn(listResponseEntity);

    }

    private void setMockitoGetTeamsForCompanyResponse(List<Team> teamListResponse) {
        ResponseEntity<List<Team>> listResponseEntity = new ResponseEntity<>(teamListResponse, HttpStatus.OK);
        Mockito.when(restTemplateMock.exchange(
                Mockito.eq(cinodeApi.getBaseUrlForCompany() + "/teams/"),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(HttpEntity.class),
                ArgumentMatchers.<ParameterizedTypeReference<List<Team>>>any())).thenReturn(listResponseEntity);


    }


    private void setMockitoGetTeamsForUserResponse(List<Team> teamListResponse) {
        ResponseEntity<List<Team>> listResponseEntity = new ResponseEntity<>(teamListResponse, HttpStatus.OK);
        Mockito.when(restTemplateMock.exchange(
                Mockito.eq(cinodeApi.getBaseUrlForCompany() + "/users/" + 1 + "/teams/"),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(HttpEntity.class),
                ArgumentMatchers.<ParameterizedTypeReference<List<Team>>>any())).thenReturn(listResponseEntity);

    }

}