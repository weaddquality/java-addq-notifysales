package se.addq.notifysales.notification;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;
import se.addq.notifysales.cinode.model.AssignmentResponse;
import se.addq.notifysales.notification.model.NotificationRepoData;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
public class NotificationServiceApiTest {

    @Mock
    private NotificationHandler notificationHandler;


    @Mock
    private AllocationResponsibleHandler allocationResponsibleHandler;

    @Mock
    private AssignmentHandler assignmentHandler;

    private NotificationServiceApi notificationServiceApi;

    @Before
    public void setup() {
        notificationServiceApi = new NotificationService(notificationHandler,
                allocationResponsibleHandler, assignmentHandler);
    }

    @Test
    public void updateAssignmentsToNotifyIsToBeNotifiedIsTrue() {
        Mockito.when(assignmentHandler.getEndingAssignments()).thenReturn(getListOfAssignmentResponse());
        Mockito.when(notificationHandler.isToBeNotified(Mockito.anyInt())).thenReturn(true);
        notificationServiceApi.updateAssignmentsToNotify();
        verify(notificationHandler, times(1)).addAssignmentsToNotificationList(Mockito.any());
        verify(allocationResponsibleHandler, times(1)).setAllocationResponsible(Mockito.any());
        verify(notificationHandler, times(1)).assignmentsToNotifyAdd(Mockito.any());
    }

    @Test
    public void updateAssignmentsToNotifyIsToBeNotifiedIsFalse() {
        Mockito.when(assignmentHandler.getEndingAssignments()).thenReturn(getListOfAssignmentResponse());
        Mockito.when(notificationHandler.isToBeNotified(Mockito.anyInt())).thenReturn(false);
        notificationServiceApi.updateAssignmentsToNotify();
        verify(notificationHandler, times(1)).addAssignmentsToNotificationList(Mockito.any());
        verify(allocationResponsibleHandler, times(1)).setAllocationResponsible(Mockito.any());
        verify(notificationHandler, times(1)).assignmentsToNotifyAdd(Mockito.any());
    }

    private List<AssignmentResponse> getListOfAssignmentResponse() {
        List<AssignmentResponse> assignmentResponseList = new ArrayList<>();
        AssignmentResponse assignmentResponse = new AssignmentResponse();
        assignmentResponse.setEndDate(LocalDateTime.now().toString());
        assignmentResponse.setId(123);
        assignmentResponse.setDescription("The thing");
        assignmentResponse.setCustomerId(12);
        assignmentResponse.setCompanyId(109);
        assignmentResponseList.add(assignmentResponse);
        return assignmentResponseList;
    }

    @Test
    public void getListOfNotifiedAssignmentsAsJsonPrettyPrintString() {
        NotificationRepoData notificationRepoData = new NotificationRepoData();
        notificationRepoData.setId(1L);
        notificationRepoData.setMessage("Hej");
        notificationRepoData.setAssignmentId(1);
        List<NotificationRepoData> notificationRepoDataList = new ArrayList<>();
        notificationRepoDataList.add(notificationRepoData);
        Mockito.when(notificationHandler.getAlreadyNotifiedAssignments()).thenReturn(notificationRepoDataList);
        String assignments = notificationServiceApi.getListOfNotifiedAssignments();
        assertThat(assignments).isEqualTo("[ {" + System.lineSeparator() +
                "  \"id\" : 1," + System.lineSeparator() +
                "  \"notifiedTime\" : null," + System.lineSeparator() +
                "  \"assignmentId\" : 1," + System.lineSeparator() +
                "  \"message\" : \"Hej\"" + System.lineSeparator() +
                "} ]");
    }


}
