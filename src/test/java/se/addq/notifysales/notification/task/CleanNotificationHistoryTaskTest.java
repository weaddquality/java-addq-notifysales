package se.addq.notifysales.notification.task;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;
import se.addq.notifysales.notification.NotificationHandler;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
public class CleanNotificationHistoryTaskTest {


    @Mock
    private NotificationHandler notificationHandler;


    private ResetNotificationHistoryTask resetNotificationHistoryTask;

    @Before
    public void setup() {
        resetNotificationHistoryTask = new ResetNotificationHistoryTask(notificationHandler);
    }

    @Test
    public void cleanNotificationSentListAndRemoveFromDb() {
        resetNotificationHistoryTask.cleanNotificationSentListAndRemoveFromDb();
        verify(notificationHandler, times(1)).clearSendNotificationHistory();
    }
}