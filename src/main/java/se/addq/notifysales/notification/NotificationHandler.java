package se.addq.notifysales.notification;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.addq.notifysales.cinode.CinodeApi;
import se.addq.notifysales.cinode.model.AssignmentResponse;
import se.addq.notifysales.cinode.model.ProjectAssignmentResponse;
import se.addq.notifysales.notification.model.MissingDataType;
import se.addq.notifysales.notification.model.NotificationData;
import se.addq.notifysales.notification.model.NotificationRepoData;
import se.addq.notifysales.notification.repository.NotificationRepository;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class NotificationHandler {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final List<NotificationRepoData> alreadyNotifiedRepoDataList;

    private final NotificationRepository notificationRepository;

    private final List<NotificationData> incompleteNotificationDataToBeRemoved = new ArrayList<>();

    private MissingDataHandler missingDataHandler;

    private CinodeApi cinodeApi;

    private final List<NotificationData> assignmentsToNotifyList = Collections.synchronizedList(new ArrayList<>());

    @Autowired
    public NotificationHandler(NotificationRepository notificationRepository, MissingDataHandler missingDataHandler, CinodeApi cinodeApi) {
        this.notificationRepository = notificationRepository;
        this.missingDataHandler = missingDataHandler;
        this.cinodeApi = cinodeApi;
        this.alreadyNotifiedRepoDataList = getPersistedNotifiedAssignments();
    }

    public void addAndPersistNotificationStatus(NotificationData notificationData, String message) {
        log.info("Save notification data to storage");
        NotificationRepoData notificationRepoData = new NotificationRepoData();
        notificationRepoData.setAssignmentId(notificationData.getAssignmentId());
        notificationRepoData.setNotifiedTime(LocalDateTime.now());
        notificationRepoData.setMessage(message);
        notificationRepository.saveNotificationData(notificationRepoData);
        alreadyNotifiedRepoDataList.add(notificationRepoData);
    }


    List<NotificationRepoData> getAlreadyNotifiedAssignments() {
        return alreadyNotifiedRepoDataList;
    }

    public List<NotificationData> getAssignmentsToNotifyList() {
        return assignmentsToNotifyList;
    }


    public void clearAssignmentsToNotify() {
        log.info("Will clear assignments notified");
        assignmentsToNotifyList.clear();
    }

    void assignmentsToNotifyAdd(List<NotificationData> notificationDataList) {
        assignmentsToNotifyList.addAll(notificationDataList);
    }

    boolean isToBeNotified(int assignmentId) {
        return !isAlreadyNotifiedOrToBeNotified(assignmentId) && !missingDataHandler.isIncompleteDataForNotification(assignmentId);
    }

    List<NotificationData> addAssignmentsToNotificationList(List<AssignmentResponse> filteredEndingAssignments) {
        List<NotificationData> notificationDataList = new ArrayList<>();
        incompleteNotificationDataToBeRemoved.clear();
        for (AssignmentResponse assignmentResponse : filteredEndingAssignments) {
            NotificationData notificationData = addAssignmentDataToNotification(assignmentResponse);
            notificationDataList.add(notificationData);
        }
        notificationDataList.removeAll(incompleteNotificationDataToBeRemoved);
        return notificationDataList;
    }

    public void clearSendNotificationHistory() {
        clearDbFromAssignments();
        clearAlreadyNotifiedAssignments();
    }

    private void clearAlreadyNotifiedAssignments() {
        log.info("Will clear all already notified assignments in memory");
        alreadyNotifiedRepoDataList.clear();
    }

    private void clearDbFromAssignments() {
        notificationRepository.deleteNotifications();
    }


    private NotificationData addAssignmentDataToNotification(AssignmentResponse assignmentResponse) {
        NotificationData notificationData = new NotificationData();
        notificationData.setAssignmentId(assignmentResponse.getId());
        notificationData.setProjectId(assignmentResponse.getProjectId());
        notificationData.setAssignmentTitle(assignmentResponse.getTitle());
        notificationData.setEndDate(assignmentResponse.getEndDate());
        notificationData.setStartDate(assignmentResponse.getStartDate());
        notificationData.setSalesManager(assignmentResponse.getSalesManager());

        log.info("Get project assignment for project {} and assignment Id {}", notificationData);
        ProjectAssignmentResponse projectAssignmentResponse = cinodeApi.getProjectAssignment(assignmentResponse.getProjectId(), assignmentResponse.getId());

        if (projectAssignmentResponse != null) {
            if (projectAssignmentResponse.getAssigned() != null) {
                notificationData.getAssignmentConsultant().setFirstName(projectAssignmentResponse.getAssigned().getFirstName());
                notificationData.getAssignmentConsultant().setLastName(projectAssignmentResponse.getAssigned().getLastName());
                notificationData.getAssignmentConsultant().setUserId(projectAssignmentResponse.getAssigned().getId());
                notificationData.getAssignmentCustomer().setId(projectAssignmentResponse.getCustomer().getId());
                notificationData.getAssignmentCustomer().setName(projectAssignmentResponse.getCustomer().getName());
                return notificationData;
            }
        }
        log.warn("Missing assigned for {} will remove from list to notify", notificationData);
        String assignmentTitle = (assignmentResponse.getTitle() == null) ? "N/A" : assignmentResponse.getTitle();
        missingDataHandler.addMissingData(notificationData, MissingDataType.MISSING_ASSIGNED, assignmentTitle);
        incompleteNotificationDataToBeRemoved.add(notificationData);
        return notificationData;
    }

    private boolean isAlreadyNotifiedOrToBeNotified(int assignmentId) {
        for (NotificationRepoData assignmentNotified : getAlreadyNotifiedAssignments())
            if (assignmentId == assignmentNotified.getAssignmentId()) {
                log.debug("AssignmentResponse already notified {}", assignmentId);
                return true;
            }
        for (NotificationData assignmentsToNotify : getAssignmentsToNotifyList()) {
            if (assignmentId == assignmentsToNotify.getAssignmentId()) {
                log.debug("AssignmentResponse already to be notified {}", assignmentId);
                return true;
            }
        }
        log.debug("AssignmentResponse not already notified or waiting to be notified {}", assignmentId);
        return false;
    }


    private List<NotificationRepoData> getPersistedNotifiedAssignments() {
        return notificationRepository.findAllNotificationData();
    }


}
