package se.addq.notifysales.notification;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import se.addq.notifysales.cinode.CinodeApi;
import se.addq.notifysales.cinode.model.ProjectList;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class AssignmentHandlerTest {

    @Mock
    private CinodeApi cinodeApi;

    private List<ProjectList> projectListList;

    @Before
    public void setup() {
        projectListList = new ArrayList<>();
        projectListList.add(new ProjectList(123));
        projectListList.add(new ProjectList(124));
        projectListList.add(new ProjectList(125));
        Mockito.when(cinodeApi.getProjects()).thenReturn(projectListList);
    }

    @Test
    public void fetchedSubProjectListWithReversedOrderOnId() {
        AssignmentHandler assignmentHandler = new AssignmentHandler(cinodeApi);
        assignmentHandler.setNumberOfProjectsToFetch(2);

        List<Integer> sublistToCheckForAssignments = assignmentHandler.getProjectSublistToCheckForAssignments();
        assertThat(sublistToCheckForAssignments.get(0)).isEqualTo(125);
        assertThat(sublistToCheckForAssignments.get(1)).isEqualTo(124);
    }

    @Test
    public void fetchedSubProjectListLessLeftThanNumbersToFetch() {
        int expectedSize = projectListList.size();
        AssignmentHandler assignmentHandler = new AssignmentHandler(cinodeApi);
        assignmentHandler.setNumberOfProjectsToFetch(expectedSize + 5);

        List<Integer> sublistToCheckForAssignments = assignmentHandler.getProjectSublistToCheckForAssignments();
        assertThat(sublistToCheckForAssignments.size()).isEqualTo(expectedSize);
    }
}
