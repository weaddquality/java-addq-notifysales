package se.addq.notifysales.notification;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;
import se.addq.notifysales.cinode.CinodeApi;
import se.addq.notifysales.cinode.model.Assigned;
import se.addq.notifysales.cinode.model.AssignmentResponse;
import se.addq.notifysales.cinode.model.Customer;
import se.addq.notifysales.cinode.model.ProjectAssignmentResponse;
import se.addq.notifysales.notification.model.NotificationData;
import se.addq.notifysales.notification.model.NotificationRepoData;
import se.addq.notifysales.notification.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
public class NotificationHandlerTest {

    private static final int ASSIGNMENT_ID_TO_BE_ALREADY_NOTIFIED = 123;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private MissingDataHandler missingDataHandler;

    @Mock
    private CinodeApi cinodeApi;

    private NotificationHandler notificationHandler;

    @Before
    public void setUp() {
        notificationHandler = new NotificationHandler(notificationRepository, missingDataHandler, cinodeApi);
        Mockito.when(notificationRepository.findAllNotificationData()).thenReturn(new ArrayList<>());
        Mockito.when(cinodeApi.getProjectAssignment(Mockito.anyInt(), Mockito.anyInt())).thenReturn(getProjectAssignment());
    }


    @Test
    public void addAndPersistNotificationStatusDataStoredInRepoList() {
        notificationHandler.addAndPersistNotificationStatus(getNotificationDataList().get(0), "Hej hej");
        verify(notificationRepository, times(1)).saveNotificationData(Mockito.any());
        List<NotificationRepoData> notificationRepoDataList = notificationHandler.getAlreadyNotifiedAssignments();
        assertThat(notificationRepoDataList.get(0).getAssignmentId()).isEqualTo(getNotificationDataList().get(0).getAssignmentId());
    }


    @Test
    public void getAssignmentsToNotify() {
        notificationHandler.assignmentsToNotifyAdd(getNotificationDataList());
        List<NotificationData> list = notificationHandler.getAssignmentsToNotifyList();
        assertThat(list.size()).isOne();
    }

    @Test
    public void clearAssignmentsToNotify() {
        notificationHandler.assignmentsToNotifyAdd(getNotificationDataList());
        int sizeBefore = notificationHandler.getAssignmentsToNotifyList().size();
        notificationHandler.clearAssignmentsToNotify();
        int sizeAfter = notificationHandler.getAssignmentsToNotifyList().size();
        assertThat(sizeAfter).isZero();
        assertThat(sizeAfter).isLessThan(sizeBefore);
    }


    @Test
    public void assignmentsAddedToNotificationListRemovedAndAddedToMissingDataIfAssignmentResponseNullAssigned() {
        ProjectAssignmentResponse projectAssignmentResponse = getProjectAssignment();
        projectAssignmentResponse.setAssigned(null);
        Mockito.when(cinodeApi.getProjectAssignment(Mockito.anyInt(), Mockito.anyInt())).thenReturn(projectAssignmentResponse);
        List<NotificationData> list = notificationHandler.addAssignmentsToNotificationList(getAssignmentResponse());
        assertThat(list.size()).isZero();
        verify(missingDataHandler, times(1)).addMissingAssignedForAssignment(Mockito.any(), Mockito.any());
    }

    @Test
    public void assignmentsAddedToNotificationListFromAssignmentResponse() {
        List<NotificationData> list = notificationHandler.addAssignmentsToNotificationList(getAssignmentResponse());
        assertThat(list.size()).isOne();
    }


    @Test
    public void isToBeNotifiedReturnsFalseWhenInToBeNotifiedList() {
        notificationHandler.assignmentsToNotifyAdd(getNotificationDataList());
        boolean isToBeNotified = notificationHandler.isToBeNotified(ASSIGNMENT_ID_TO_BE_ALREADY_NOTIFIED);
        assertThat(isToBeNotified).isFalse();
    }

    @Test
    public void isToBeNotifiedReturnsFalseWhenInAlreadyNotifiedList() {
        notificationHandler.addAndPersistNotificationStatus(getNotificationDataList().get(0), "Hej hej");
        boolean isToBeNotified = notificationHandler.isToBeNotified(getNotificationDataList().get(0).getAssignmentId());
        assertThat(isToBeNotified).isFalse();
    }

    @Test
    public void isToBeNotifiedReturnsTrueWhenNotInToBeNotifiedList() {
        notificationHandler.assignmentsToNotifyAdd(getNotificationDataList());
        boolean isToBeNotified = notificationHandler.isToBeNotified(ASSIGNMENT_ID_TO_BE_ALREADY_NOTIFIED + 1);
        assertThat(isToBeNotified).isTrue();
    }

    private List<NotificationData> getNotificationDataList() {
        List<NotificationData> notificationDataList = new ArrayList<>();
        NotificationData notificationData = new NotificationData();
        notificationData.setAssignmentId(ASSIGNMENT_ID_TO_BE_ALREADY_NOTIFIED);
        notificationDataList.add(notificationData);
        return notificationDataList;
    }


    private List<AssignmentResponse> getAssignmentResponse() {
        List<AssignmentResponse> assignmentResponseList = new ArrayList<>();
        AssignmentResponse assignmentResponse = new AssignmentResponse();
        assignmentResponse.setCompanyId(109);
        assignmentResponse.setCustomerId(12);
        assignmentResponse.setDescription("Tester");
        assignmentResponse.setEndDate(LocalDateTime.now().toString());
        assignmentResponse.setId(ASSIGNMENT_ID_TO_BE_ALREADY_NOTIFIED);
        assignmentResponseList.add(assignmentResponse);
        return assignmentResponseList;
    }


    private ProjectAssignmentResponse getProjectAssignment() {
        ProjectAssignmentResponse projectAssignmentResponse = new ProjectAssignmentResponse();
        Assigned assigned = new Assigned();
        assigned.setFirstName("Kalle");
        assigned.setLastName("Kula");
        assigned.setCompanyId(1);
        assigned.setUserId("12");
        projectAssignmentResponse.setAssigned(assigned);
        Customer customer = new Customer();
        customer.setName("The Big Company");
        customer.setId(123);
        projectAssignmentResponse.setCustomer(customer);
        return projectAssignmentResponse;
    }

}