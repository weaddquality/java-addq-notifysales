package se.addq.notifysales.notification.task;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import se.addq.notifysales.notification.NotificationHandler;
import se.addq.notifysales.notification.model.NotificationData;
import se.addq.notifysales.slack.SlackApi;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
public class NotificationPushTaskTest {

    @Mock
    private NotificationHandler notificationHandler;

    @Mock
    private SlackApi slackApi;

    private NotificationPushTask notificationPushTask;

    @Before
    public void setup() {
        notificationPushTask = new NotificationPushTask(slackApi, notificationHandler);
        ReflectionTestUtils.setField(notificationPushTask, "slackWebhookUrl", "http://dummy");
    }

    @Test
    public void triggerNotificationIfReadyToBeNotifiedIsTrueAndSlackNotificationSendIsSuccessful() {
        NotificationData notificationData = new NotificationData();
        notificationData.setAssignmentTitle("Testare");
        notificationData.setProjectId(1);
        notificationData.setReadyToBeNotified(true);
        List<NotificationData> notificationDataList = new ArrayList<>();
        notificationDataList.add(notificationData);
        Mockito.when(notificationHandler.getAssignmentsToNotifyList()).thenReturn(notificationDataList);
        Mockito.when(slackApi.sendNotification(Mockito.any(), Mockito.anyString())).thenReturn(true);


        notificationPushTask.notifyAboutAssignmentsEnding();
        verify(notificationHandler, times(1)).addAndPersistNotificationStatus(Mockito.any(), Mockito.any());
        verify(notificationHandler, times(1)).clearAssignmentsToNotify();
    }

    @Test
    public void triggerNotificationIfAssignmentsEndingReadyToBeNotifiedIsFalse() {
        NotificationData notificationData = new NotificationData();
        notificationData.setAssignmentTitle("Testare");
        notificationData.setProjectId(1);
        notificationData.setReadyToBeNotified(false);
        List<NotificationData> notificationDataList = new ArrayList<>();
        notificationDataList.add(notificationData);
        Mockito.when(notificationHandler.getAssignmentsToNotifyList()).thenReturn(notificationDataList);

        notificationPushTask.notifyAboutAssignmentsEnding();
        verify(notificationHandler, times(0)).addAndPersistNotificationStatus(Mockito.any(), Mockito.any());
        verify(notificationHandler, times(1)).clearAssignmentsToNotify();
    }


}
