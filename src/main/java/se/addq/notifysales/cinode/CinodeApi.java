package se.addq.notifysales.cinode;

import se.addq.notifysales.cinode.model.*;

import java.util.List;

public interface CinodeApi {

    TokenResponse getToken();

    List<CompaniesResponse> getCompanies();

    List<ProjectList> getProjects();

    ProjectResponse getProject(int id);

    ProjectAssignmentResponse getProjectAssignment(int projectId, int assignmentId);

    List<Team> getTeamsForUser(int userId);

    List<Team> getTeamsForCompany();

    String getBaseUrlForCompany();
}
