package se.addq.notifysales.notification.task;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import se.addq.notifysales.notification.MissingDataHandler;
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

    private MissingDataPushTask missingDataPushTask;

    @Before
    public void setup() {
        missingDataPushTask = new MissingDataPushTask(slackApi, missingDataHandler);
        ReflectionTestUtils.setField(missingDataPushTask, "slackWebhookUrl", "http://dummy");
    }


    @Test
    public void triggerNotificationIfMissingDataNotifiedIsFalseAndNotificationSentIsTrue() {
        MissingNotificationData missingNotificationData = new MissingNotificationData();
        missingNotificationData.setMissingData("Saknas team testa");
        missingNotificationData.setMissingdataType(MissingDataType.MISSING_ALLOCATION_RESPONSIBLE);
        missingNotificationData.setAssignmentId(1);
        missingNotificationData.setNotified(false);
        List<MissingNotificationData> missingNotificationDataList = new ArrayList<>();
        missingNotificationDataList.add(missingNotificationData);
        Mockito.when(missingDataHandler.getMissingDataReadyToBeNotifiedList()).thenReturn(missingNotificationDataList);
        Mockito.when(slackApi.sendNotification(Mockito.any(), Mockito.anyString())).thenReturn(true);

        missingDataPushTask.notifyAboutAssignmentsWithMissingData();
        verify(missingDataHandler, times(1)).persistMissingDataNotifications(Mockito.any());
        verify(missingDataHandler, times(1)).addAlreadyNotifiedMissingData(Mockito.any());
        verify(missingDataHandler, times(1)).clearMissingDataNotifiedList(Mockito.any());
    }

    @Test
    public void triggerNotificationIfMissingDataSetNotifiedIsFalseAndNotificationSentIsFalse() {
        MissingNotificationData missingNotificationData = new MissingNotificationData();
        missingNotificationData.setMissingData("Saknas team testa");
        missingNotificationData.setMissingdataType(MissingDataType.MISSING_ALLOCATION_RESPONSIBLE);
        missingNotificationData.setAssignmentId(1);
        missingNotificationData.setNotified(false);
        List<MissingNotificationData> missingNotificationDataList = new ArrayList<>();
        missingNotificationDataList.add(missingNotificationData);
        Mockito.when(missingDataHandler.getMissingDataReadyToBeNotifiedList()).thenReturn(missingNotificationDataList);
        Mockito.when(slackApi.sendNotification(Mockito.any(), Mockito.anyString())).thenReturn(false);

        missingDataPushTask.notifyAboutAssignmentsWithMissingData();
        verify(missingDataHandler, times(0)).persistMissingDataNotifications(Mockito.any());
        verify(missingDataHandler, times(0)).addAlreadyNotifiedMissingData(Mockito.any());
        verify(missingDataHandler, times(1)).clearMissingDataNotifiedList(Mockito.any());
    }

    @Test
    public void triggerNotificationIfMissingDataSetNotifiedIsTrue() {
        MissingNotificationData missingNotificationData = new MissingNotificationData();
        missingNotificationData.setMissingData("Saknas team testa");
        missingNotificationData.setMissingdataType(MissingDataType.MISSING_ALLOCATION_RESPONSIBLE);
        missingNotificationData.setAssignmentId(1);
        missingNotificationData.setNotified(true);
        List<MissingNotificationData> missingNotificationDataList = new ArrayList<>();
        missingNotificationDataList.add(missingNotificationData);
        Mockito.when(missingDataHandler.getMissingDataReadyToBeNotifiedList()).thenReturn(missingNotificationDataList);

        missingDataPushTask.notifyAboutAssignmentsWithMissingData();
        verify(missingDataHandler, times(0)).persistMissingDataNotifications(Mockito.any());
        verify(missingDataHandler, times(0)).addAlreadyNotifiedMissingData(Mockito.any());
        verify(missingDataHandler, times(1)).clearMissingDataNotifiedList(Mockito.any());
    }

}
