package se.addq.notifysales.notification;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import se.addq.notifysales.cinode.CinodeApi;
import se.addq.notifysales.cinode.model.AssignmentResponse;
import se.addq.notifysales.cinode.model.ProjectList;
import se.addq.notifysales.cinode.model.ProjectResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class AssignmentHandlerTest {

    @Mock
    private CinodeApi cinodeApi;

    private List<ProjectList> projectListList;

    private AssignmentHandler assignmentHandler;

    @Before
    public void setup() {
        assignmentHandler = new AssignmentHandler(cinodeApi);
        setExternalProperties();
        projectListList = new ArrayList<>();
        projectListList.add(new ProjectList(123));
        projectListList.add(new ProjectList(124));
        projectListList.add(new ProjectList(125));
        Mockito.when(cinodeApi.getProjects()).thenReturn(projectListList);
    }

    private void setExternalProperties() {
        ReflectionTestUtils.setField(assignmentHandler, "numberOfProjectsToFetch", 10);
        ReflectionTestUtils.setField(assignmentHandler, "weeksBeforeAssignmentEndsToNotify", 8);
        ReflectionTestUtils.setField(assignmentHandler, "weeksAfterAssignmentEndsToNotify", 0);
    }


    @Test
    public void shouldGetEmptyListReturnedWhenCinodeGetProjectsReturnsNull() {
        Mockito.when(cinodeApi.getProjects()).thenReturn(null);
        List<AssignmentResponse> assignmentResponseList = assignmentHandler.getEndingAssignments();
        assertThat(assignmentResponseList).isEmpty();
    }



    @Test
    public void shouldGetEmptyListReturnedWhenCinodeGetProjectReturnsNull() {
        Mockito.when(cinodeApi.getProject(Mockito.anyInt())).thenReturn(null);
        List<AssignmentResponse> assignmentResponseList = assignmentHandler.getEndingAssignments();
        assertThat(assignmentResponseList).isEmpty();
    }

    @Test
    public void shouldGetListWhenEndDateToday() {
        ProjectResponse projectResponse = getProjectResponseEndingToday();
        Mockito.when(cinodeApi.getProject(Mockito.anyInt())).thenReturn(projectResponse);
        List<AssignmentResponse> assignmentResponseList = assignmentHandler.getEndingAssignments();
        assertThat(assignmentResponseList.size()).isEqualTo(projectListList.size());
    }

    @Test
    public void shouldGetListEndDateWithinEightWeeks() {
        ProjectResponse projectResponse = getProjectResponseEndingWithinEightWeeks();
        Mockito.when(cinodeApi.getProject(Mockito.anyInt())).thenReturn(projectResponse);
        List<AssignmentResponse> assignmentResponseList = assignmentHandler.getEndingAssignments();
        assertThat(assignmentResponseList.size()).isEqualTo(projectListList.size());
    }

    @Test
    public void shouldNotGetListWhenEndDateMoreThanEightWeeksFromToday() {
        ProjectResponse projectResponse = getProjectResponseEndingInNineWeeks();
        Mockito.when(cinodeApi.getProject(Mockito.anyInt())).thenReturn(projectResponse);
        List<AssignmentResponse> assignmentResponseList = assignmentHandler.getEndingAssignments();
        assertThat(assignmentResponseList.size()).isEqualTo(0);
    }

    @Test
    public void shouldNotGetListWhenEndDateBeforeToday() {
        ProjectResponse projectResponse = getProjectResponseEndingBeforeToday();
        Mockito.when(cinodeApi.getProject(Mockito.anyInt())).thenReturn(projectResponse);
        List<AssignmentResponse> assignmentResponseList = assignmentHandler.getEndingAssignments();
        assertThat(assignmentResponseList.size()).isEqualTo(0);
    }


    @Test
    public void shouldGetEmptyListWhenNoAssignmentsInCinodeGetProjectResponse() {
        ProjectResponse projectResponse = getProjectResponseEndingToday();
        projectResponse.setAssignmentResponses(null);
        Mockito.when(cinodeApi.getProject(Mockito.anyInt())).thenReturn(projectResponse);
        List<AssignmentResponse> assignmentResponseList = assignmentHandler.getEndingAssignments();
        assertThat(assignmentResponseList).isEmpty();
    }

    private ProjectResponse getProjectResponseEndingToday() {
        ProjectResponse projectResponse = new ProjectResponse();
        AssignmentResponse assignmentResponseZeroWeeks = new AssignmentResponse();
        assignmentResponseZeroWeeks.setId(124);
        assignmentResponseZeroWeeks.setEndDate(LocalDateTime.now().plusWeeks(0).plusMinutes(10).toString());
        List<AssignmentResponse> assignmentResponseList = new ArrayList<>();
        assignmentResponseList.add(assignmentResponseZeroWeeks);
        projectResponse.setAssignmentResponses(assignmentResponseList);
        return projectResponse;
    }

    private ProjectResponse getProjectResponseEndingWithinEightWeeks() {
        ProjectResponse projectResponse = new ProjectResponse();
        AssignmentResponse assignmentResponseEightWeeks = new AssignmentResponse();
        assignmentResponseEightWeeks.setId(123);
        assignmentResponseEightWeeks.setEndDate(LocalDateTime.now().plusWeeks(8).toString());
        List<AssignmentResponse> assignmentResponseList = new ArrayList<>();
        assignmentResponseList.add(assignmentResponseEightWeeks);
        projectResponse.setAssignmentResponses(assignmentResponseList);
        return projectResponse;
    }

    private ProjectResponse getProjectResponseEndingInNineWeeks() {
        ProjectResponse projectResponse = new ProjectResponse();
        AssignmentResponse assignmentResponseNineWeeks = new AssignmentResponse();
        assignmentResponseNineWeeks.setId(123);
        assignmentResponseNineWeeks.setEndDate(LocalDateTime.now().plusWeeks(9).toString());
        List<AssignmentResponse> assignmentResponseList = new ArrayList<>();
        assignmentResponseList.add(assignmentResponseNineWeeks);
        projectResponse.setAssignmentResponses(assignmentResponseList);
        return projectResponse;
    }

    private ProjectResponse getProjectResponseEndingBeforeToday() {
        ProjectResponse projectResponse = new ProjectResponse();
        AssignmentResponse assignmentResponseBeforeToday = new AssignmentResponse();
        assignmentResponseBeforeToday.setId(124);
        assignmentResponseBeforeToday.setEndDate(LocalDateTime.now().toString());
        List<AssignmentResponse> assignmentResponseList = new ArrayList<>();
        assignmentResponseList.add(assignmentResponseBeforeToday);
        projectResponse.setAssignmentResponses(assignmentResponseList);
        return projectResponse;
    }
}
