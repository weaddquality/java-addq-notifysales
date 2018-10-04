package se.addq.notifysales.notification;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;
import se.addq.notifysales.notification.model.MissingDataType;
import se.addq.notifysales.notification.model.MissingNotificationData;
import se.addq.notifysales.slack.SlackApi;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
public class MissingDataPushTaskTest {

    @Mock
    private MissingDataHandler missingDataHandler;

    @Mock
    private SlackApi slackApi;


    @Test
    public void triggerNotificationIfMissingDataNotifiedIsFalse() {
        MissingNotificationData missingNotificationData = new MissingNotificationData();
        missingNotificationData.setMissingData("Saknas team testa");
        missingNotificationData.setMissingdataType(MissingDataType.MISSING_ALLOCATION_RESPONSIBLE);
        missingNotificationData.setAssignmentId(1);
        missingNotificationData.setNotified(false);
        List<MissingNotificationData> missingNotificationDataList = new ArrayList<>();
        missingNotificationDataList.add(missingNotificationData);
        Mockito.when(missingDataHandler.getMissingDataNotifyList()).thenReturn(missingNotificationDataList);

        MissingDataPushTask notificationPushTask = new MissingDataPushTask(slackApi, missingDataHandler);
        notificationPushTask.notifyAboutAssignmentsWithMissingData();
        verify(missingDataHandler, times(1)).persistMissingDataNotifications(Mockito.any());
        verify(missingDataHandler, times(1)).clearMissingDataNotifyList();
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
        Mockito.when(missingDataHandler.getMissingDataNotifyList()).thenReturn(missingNotificationDataList);

        MissingDataPushTask notificationPushTask = new MissingDataPushTask(slackApi, missingDataHandler);
        notificationPushTask.notifyAboutAssignmentsWithMissingData();
        verify(missingDataHandler, times(0)).persistMissingDataNotifications(Mockito.any());
        verify(missingDataHandler, times(1)).clearMissingDataNotifyList();
    }

}
