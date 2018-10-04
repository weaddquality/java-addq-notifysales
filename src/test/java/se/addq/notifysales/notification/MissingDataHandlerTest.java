package se.addq.notifysales.notification;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;
import se.addq.notifysales.notification.model.MissingDataType;
import se.addq.notifysales.notification.model.MissingNotificationData;
import se.addq.notifysales.notification.model.NotificationData;
import se.addq.notifysales.notification.repository.MissingDataRepository;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@RunWith(SpringRunner.class)
public class MissingDataHandlerTest {

    private static final int ASSIGNMENT_ID_IN_MISSING_DATA = 123;
    @Mock
    private MissingDataRepository missingDataRepositoryMock;

    private MissingDataHandler missingDataHandler;

    @Before
    public void setUp() {
        Mockito.when(missingDataRepositoryMock.findAllNotificationData()).thenReturn(getMissingNotificationTestDataList());
        missingDataHandler = new MissingDataHandler(missingDataRepositoryMock);
    }

    private List<MissingNotificationData> getMissingNotificationTestDataList() {
        List<MissingNotificationData> missingNotificationDataList = new ArrayList<>();
        MissingNotificationData missingNotificationData = new MissingNotificationData();
        missingNotificationData.setId(10L);
        missingNotificationData.setAssignmentId(ASSIGNMENT_ID_IN_MISSING_DATA);
        missingNotificationDataList.add(missingNotificationData);
        return missingNotificationDataList;
    }

    @Test
    public void isIncompleteDataForNotificationIsTrue() {
        boolean isMissingData = missingDataHandler.isIncompleteDataForNotification(ASSIGNMENT_ID_IN_MISSING_DATA);
        assertThat(isMissingData).isTrue();
    }

    @Test
    public void isIncompleteDataForNotificationIsFalse() {
        boolean isMissingData = missingDataHandler.isIncompleteDataForNotification(ASSIGNMENT_ID_IN_MISSING_DATA + 1);
        assertThat(isMissingData).isFalse();
    }

    @Test
    public void addTeamIsMissingForUser() {
        int assignmentIdAdded = 130;
        NotificationData notificationData = getNotificationTestData();
        notificationData.setAssignmentId(assignmentIdAdded);
        missingDataHandler.addTeamIsMissingForUser(notificationData);
        List<MissingNotificationData> missingNotificationDataList = missingDataHandler.getMissingDataNotifyList();
        MissingNotificationData missingNotificationData = missingNotificationDataList.stream().filter(f -> f.getAssignmentId() == assignmentIdAdded).findFirst().orElse(null);
        assertThat(missingNotificationData).isNotNull();
        assertThat(missingNotificationData.getMissingdataType()).isEqualTo(MissingDataType.MISSING_TEAM_FOR_USER);
    }

    @Test
    public void addAllocationResponsibleIsMissingForTeam() {
        NotificationData notificationData = getNotificationTestData();
        int assignmentIdAdded = 131;
        notificationData.setAssignmentId(assignmentIdAdded);
        missingDataHandler.addAllocationResponsibleIsMissingForTeam(notificationData, "Bad data");
        List<MissingNotificationData> missingNotificationDataList = missingDataHandler.getMissingDataNotifyList();
        MissingNotificationData missingNotificationData = missingNotificationDataList.stream().filter(f -> f.getAssignmentId() == assignmentIdAdded).findFirst().orElse(null);
        assertThat(missingNotificationData).isNotNull();
        assertThat(missingNotificationData.getMissingdataType()).isEqualTo(MissingDataType.MISSING_ALLOCATION_RESPONSIBLE);
    }

    @Test
    public void addMissingAssignedForAssignment() {
        NotificationData notificationData = getNotificationTestData();
        int assignmentIdAdded = 132;
        notificationData.setAssignmentId(assignmentIdAdded);
        missingDataHandler.addMissingAssignedForAssignment(notificationData, "Bad data");
        List<MissingNotificationData> missingNotificationDataList = missingDataHandler.getMissingDataNotifyList();
        MissingNotificationData missingNotificationData = missingNotificationDataList.stream().filter(f -> f.getAssignmentId() == assignmentIdAdded).findFirst().orElse(null);
        assertThat(missingNotificationData).isNotNull();
        assertThat(missingNotificationData.getMissingdataType()).isEqualTo(MissingDataType.MISSING_ASSIGNED);
    }


    @Test
    public void removeFromMissingDataIfExisting() {
        missingDataHandler.removeFromMissingDataIfExisting(ASSIGNMENT_ID_IN_MISSING_DATA);
        verify(missingDataRepositoryMock, times(1)).delete(Mockito.any());
    }


    @Test
    public void persistMissingDataNotifications() {
        missingDataHandler.persistMissingDataNotifications(getMissingNotificationTestDataList().get(0));
        verify(missingDataRepositoryMock, times(1)).saveMissingNotificationData(Mockito.any());
    }

    @Test
    public void clearAssignmentsToNotify() {
        missingDataHandler.clearMissingDataNotifyList();
        List<MissingNotificationData> missingNotificationDataList = missingDataHandler.getMissingDataNotifyList();
        assertThat(missingNotificationDataList).isEmpty();
    }


    private NotificationData getNotificationTestData() {
        NotificationData notificationData = new NotificationData();
        notificationData.setAssignmentId(123);
        notificationData.setProjectId(12);
        notificationData.setAssignmentTitle("Tester");
        return notificationData;
    }
}