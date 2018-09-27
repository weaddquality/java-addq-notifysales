package se.addq.notifysales.notification;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;
import se.addq.notifysales.notification.model.MissingDataType;
import se.addq.notifysales.notification.model.MissingNotificationData;
import se.addq.notifysales.notification.model.NotificationData;
import se.addq.notifysales.slack.SlackApi;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
public class NotificationPushTaskTest {

    @Mock
    private NotificationService notificationService;

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
        Mockito.when(notificationService.getAssignmentsToNotify()).thenReturn(notificationDataList);
        Mockito.when(slackApi.sendNotification(Mockito.any())).thenReturn(true);

        NotificationPushTask notificationPushTask = new NotificationPushTask(slackApi, notificationService);
        notificationPushTask.notifyAboutAssignmentsEnding();
        verify(notificationService, times(1)).persistAssignmentNotified(Mockito.any(), Mockito.any());
        verify(notificationService, times(1)).clearAssignmentsToNotify();
    }

    @Test
    public void triggerNotificationIfAssignmentsEndingReadyToBeNotifiedIsFalse() {
        NotificationData notificationData = new NotificationData();
        notificationData.setAssignmentTitle("Testare");
        notificationData.setProjectId(1);
        notificationData.setReadyToBeNotified(false);
        List<NotificationData> notificationDataList = new ArrayList<>();
        notificationDataList.add(notificationData);
        Mockito.when(notificationService.getAssignmentsToNotify()).thenReturn(notificationDataList);

        NotificationPushTask notificationPushTask = new NotificationPushTask(slackApi, notificationService);
        notificationPushTask.notifyAboutAssignmentsEnding();
        verify(notificationService, times(0)).persistAssignmentNotified(Mockito.any(), Mockito.any());
        verify(notificationService, times(1)).clearAssignmentsToNotify();
    }

    @Test
    public void triggerNotificationIfMissingDataNotifiedIsFalse() {
        MissingNotificationData missingNotificationData = new MissingNotificationData();
        missingNotificationData.setMissingData("Saknas team testa");
        missingNotificationData.setMissingdataType(MissingDataType.MISSING_ALLOCATION_RESPONSIBLE);
        missingNotificationData.setAssignmentId(1);
        missingNotificationData.setNotified(false);
        List<MissingNotificationData> missingNotificationDataList = new ArrayList<>();
        missingNotificationDataList.add(missingNotificationData);
        Mockito.when(notificationService.getMissingDataForAssignments()).thenReturn(missingNotificationDataList);

        NotificationPushTask notificationPushTask = new NotificationPushTask(slackApi, notificationService);
        notificationPushTask.notifyAboutAssignmentsWithMissingData();
        verify(notificationService, times(1)).persistInfoForMissingData(Mockito.any());
        verify(notificationService, times(1)).clearMissingDataToNotify();
    }

    @Test
    public void triggerNotificationIfMissingDataNotifiedIsTrue() {
        MissingNotificationData missingNotificationData = new MissingNotificationData();
        missingNotificationData.setMissingData("Saknas team testa");
        missingNotificationData.setMissingdataType(MissingDataType.MISSING_ALLOCATION_RESPONSIBLE);
        missingNotificationData.setAssignmentId(1);
        missingNotificationData.setNotified(true);
        List<MissingNotificationData> missingNotificationDataList = new ArrayList<>();
        missingNotificationDataList.add(missingNotificationData);
        Mockito.when(notificationService.getMissingDataForAssignments()).thenReturn(missingNotificationDataList);

        NotificationPushTask notificationPushTask = new NotificationPushTask(slackApi, notificationService);
        notificationPushTask.notifyAboutAssignmentsWithMissingData();
        verify(notificationService, times(0)).persistInfoForMissingData(Mockito.any());
        verify(notificationService, times(1)).clearMissingDataToNotify();
    }

}
