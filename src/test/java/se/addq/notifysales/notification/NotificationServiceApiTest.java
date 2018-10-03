package se.addq.notifysales.notification;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;
import se.addq.notifysales.notification.model.NotificationRepoData;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
    public void updateAssignmentsToNotify() {
        notificationServiceApi.updateAssignmentsToNotify();
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
