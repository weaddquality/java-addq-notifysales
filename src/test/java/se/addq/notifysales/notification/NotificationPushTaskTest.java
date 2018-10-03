package se.addq.notifysales.notification;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;
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

    @Test
    public void triggerNotificationIfReadyToBeNotifiedIsTrueAndSlackNotificationSendIsSuccessful() {
        NotificationData notificationData = new NotificationData();
        notificationData.setAssignmentTitle("Testare");
        notificationData.setProjectId(1);
        notificationData.setReadyToBeNotified(true);
        List<NotificationData> notificationDataList = new ArrayList<>();
        notificationDataList.add(notificationData);
        Mockito.when(notificationHandler.getAssignmentsToNotify()).thenReturn(notificationDataList);
        Mockito.when(slackApi.sendNotification(Mockito.any())).thenReturn(true);

        NotificationPushTask notificationPushTask = new NotificationPushTask(slackApi, notificationHandler);
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
        Mockito.when(notificationHandler.getAssignmentsToNotify()).thenReturn(notificationDataList);

        NotificationPushTask notificationPushTask = new NotificationPushTask(slackApi, notificationHandler);
        notificationPushTask.notifyAboutAssignmentsEnding();
        verify(notificationHandler, times(0)).addAndPersistNotificationStatus(Mockito.any(), Mockito.any());
        verify(notificationHandler, times(1)).clearAssignmentsToNotify();
    }


}
