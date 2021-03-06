package se.addq.notifysales.notification.task;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;
import se.addq.notifysales.notification.NotificationServiceApi;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
public class AssignmentPollTaskTest {

    @Mock
    private NotificationServiceApi notificationService;

    @Test
    public void triggerUpdateAssignmentListToNotify() {
        AssignmentPollTask assignmentPollTask = new AssignmentPollTask(notificationService);
        assignmentPollTask.updateAssignmentListToNotifyInBatch();
        verify(notificationService, times(1)).updateAssignmentsToNotify();
    }

}
