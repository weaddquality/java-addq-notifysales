package se.addq.notifysales.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.addq.notifysales.cinode.model.Assignment;
import se.addq.notifysales.cinode.model.ProjectAssignmentResponse;
import se.addq.notifysales.cinode.model.ProjectResponse;
import se.addq.notifysales.cinode.model.Team;
import se.addq.notifysales.notification.model.AllocationResponsible;
import se.addq.notifysales.notification.model.MissingNotificationData;
import se.addq.notifysales.notification.model.NotificationData;
import se.addq.notifysales.notification.model.NotificationRepoData;
import se.addq.notifysales.utils.SleepUtil;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.List;

@Service
class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Value("${slack.notification.before.weeks}")
    private int weeksBeforeAssignmentEndsToNotify;
    @Value("${slack.notification.after.weeks}")
    private int weeksAfterAssignmentEndsToNotify;


    private final AssignmentHandler assignmentHandler;

    private final AllocationResponsibleHandler allocationResponsibleHandler;

    private final NotificationHandler notificationHandler;

    private final MissingDataHandler missingDataHandler;


    @Autowired
    public NotificationService(NotificationHandler notificationHandler, MissingDataHandler missingDataHandler, AllocationResponsibleHandler allocationResponsibleHandler, AssignmentHandler assignmentHandler) {
        this.notificationHandler = notificationHandler;
        this.missingDataHandler = missingDataHandler;
        this.allocationResponsibleHandler = allocationResponsibleHandler;
        this.assignmentHandler = assignmentHandler;
    }


    void updateAssignmentsToNotify() {
        checkForEndingAssignments();
        setNotificationDataForAssignment();
        setAllocationResponsible();
        notificationHandler.removeNotCompleteAssignments();
    }

    List<NotificationData> getAssignmentsToNotify() {
        return notificationHandler.getAssignmentsToNotify();
    }


    byte[] getAllocationConfiguration() {
        return allocationResponsibleHandler.getAllocationResponsibleListAsByteArray();
    }


    void persistInfoForMissingData(MissingNotificationData missingNotificationData) {
        missingDataHandler.persistMissingDataNotifications(missingNotificationData);
    }


    void clearMissingDataToNotify() {
        log.info("Will clear missing data assignments notified");
        missingDataHandler.clearAssignmentsToNotify();
    }

    List<MissingNotificationData> getMissingDataForAssignments() {
        return missingDataHandler.getMissingNotificationDataList();
    }

    String getListOfNotifiedAssignments() {
        return getListOfItemsAsJson(notificationHandler.getAlreadyNotifiedAssignments());
    }

    void clearAssignmentsToNotify() {
        notificationHandler.clearAssignmentsToNotify();
    }

    private void checkForEndingAssignments() {
        List<Integer> projectsResponseList = assignmentHandler.getProjectSublistToCheckForAssignments();
        for (Integer projectId : projectsResponseList) {
            ProjectResponse projectResponse = assignmentHandler.getProject(projectId);
            SleepUtil.sleepMilliSeconds(500);
            for (Assignment assignment : projectResponse.getAssignments()) {
                log.debug("Assignments: {} {} {} {}", assignment.getTitle(), assignment.getDescription(), assignment.getStartDate(), assignment.getEndDate());
                if (isAlreadyNotifiedOrToBeNotified(assignment.getId())) {
                    continue;
                }
                if (isIncompleteDataForNotification(assignment.getId())) {
                    continue;
                }
                checkIfAssignmentIsEnding(assignment);
            }
        }
    }

    @SuppressWarnings("SynchronizeOnNonFinalField")
    private void setNotificationDataForAssignment() {
        synchronized (notificationHandler.getAssignmentsToNotify()) {
            for (NotificationData notificationData : notificationHandler.getAssignmentsToNotify()) {
                log.info("Get assignment for notification data {}", notificationData);
                ProjectAssignmentResponse projectAssignmentResponse = assignmentHandler.getProjectAssignment(notificationData.getProjectId(), notificationData.getAssignmentId());
                SleepUtil.sleepMilliSeconds(500);
                if (projectAssignmentResponse.getAssigned() != null) {
                    notificationData.getAssignmentConsultant().setFirstName(projectAssignmentResponse.getAssigned().getFirstName());
                    notificationData.getAssignmentConsultant().setLastName(projectAssignmentResponse.getAssigned().getLastName());
                    notificationData.getAssignmentConsultant().setUserId(projectAssignmentResponse.getAssigned().getId());
                    notificationData.getAssignmentCustomer().setId(projectAssignmentResponse.getCustomer().getId());
                    notificationData.getAssignmentCustomer().setName(projectAssignmentResponse.getCustomer().getName());
                } else {
                    log.warn("Missing assigned for {} will remove from list to notify", notificationData);
                    notificationHandler.incompleteAssignmentAdd(notificationData);
                }
            }
        }
    }

    private void setAllocationResponsible() {
        for (NotificationData notificationData : notificationHandler.getAssignmentsToNotify()) {
            List<Team> teams = assignmentHandler.getTeamsForUser(notificationData.getAssignmentConsultant().getUserId());
            if (teams.isEmpty()) {
                log.warn("Missing team for user {} in Cinode for {} will remove from list to notify", notificationData.getAssignmentConsultant().getFirstName() + notificationData.getAssignmentConsultant().getLastName(), notificationData);
                notificationHandler.incompleteAssignmentAdd(notificationData);
                missingDataHandler.addTeamIsMissingForUser(notificationData);
                continue;
            }
            SleepUtil.sleepMilliSeconds(500);
            AllocationResponsible allocationResponsible = allocationResponsibleHandler.getAllocationResponsibleForTeam(teams.get(0));
            if (allocationResponsible.getName() == null || allocationResponsible.getName().equals("")) {
                log.warn("Missing configuration for team {}, will remove from notification list", teams.get(0).getName());
                notificationHandler.incompleteAssignmentAdd(notificationData);
                missingDataHandler.addAllocationResponsibleIsMissingForTeam(notificationData, teams.get(0).getName());
                continue;
            }
            notificationData.getAssignmentConsultant().setTeamName(teams.get(0).getName());
            notificationData.getAssignmentConsultant().setTeamId(teams.get(0).getId());
            notificationData.setAllocationResponsible(allocationResponsible);
            notificationData.setReadyToBeNotified(true);
            missingDataHandler.removeFromMissingDataIfExisting(notificationData.getAssignmentId());
        }
    }


    void persistAssignmentNotified(NotificationData notificationData, String message) {
        notificationHandler.addAndPersistNotificationStatus(notificationData, message);
    }

    private void checkIfAssignmentIsEnding(Assignment assignment) {
        if (assignment.getEndDate() == null) {
            return;
        }
        if (isAssignmentToBeNotifiedBasedOnEndDate(assignment)) {
            log.info("Found assignment ending within {} weeks. {}", weeksBeforeAssignmentEndsToNotify, assignment);
            NotificationData notificationData = new NotificationData();
            notificationData.setAssignmentId(assignment.getId());
            notificationData.setProjectId(assignment.getProjectId());
            notificationData.setAssignmentTitle(assignment.getTitle());
            notificationData.setEndDate(assignment.getEndDate());
            notificationData.setStartDate(assignment.getStartDate());
            notificationHandler.assignmentsToNotifyAdd(notificationData);
        }
    }

    private boolean isAssignmentToBeNotifiedBasedOnEndDate(Assignment assignment) {
        return assignment.getEndDate().isBefore(LocalDateTime.now().plusWeeks(weeksBeforeAssignmentEndsToNotify)) && assignment.getEndDate().isAfter(LocalDateTime.now().minusWeeks(weeksAfterAssignmentEndsToNotify));
    }

    private boolean isIncompleteDataForNotification(int assignmentId) {
        for (MissingNotificationData missingNotificationData : missingDataHandler.getMissingNotificationDataList()) {
            if (assignmentId == missingNotificationData.getAssignmentId()) {
                log.debug("Assignment has not complete data {}", assignmentId);
                return true;
            }
        }
        return false;
    }

    private boolean isAlreadyNotifiedOrToBeNotified(int assignmentId) {
        for (NotificationRepoData assignmentNotified : notificationHandler.getAlreadyNotifiedAssignments()) {
            if (assignmentId == assignmentNotified.getAssignmentId()) {
                log.debug("Assignment already notified {}", assignmentId);
                return true;
            }
        }
        for (NotificationData assignmentsToNotify : getAssignmentsToNotify()) {
            if (assignmentId == assignmentsToNotify.getAssignmentId()) {
                log.debug("Assignment already to be notified {}", assignmentId);
                return true;
            }
        }
        log.debug("Assignment not already notified or waiting to be notified {}", assignmentId);
        return false;
    }


    private <T> String getListOfItemsAsJson(List<T> listOfItems) {
        String assignmentJson = "";
        try {
            assignmentJson = new ObjectMapper().configure(SerializationFeature.INDENT_OUTPUT, true).registerModule(new JavaTimeModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false).writeValueAsString(listOfItems);
            log.debug("body {}", assignmentJson);
        } catch (JsonProcessingException e) {
            log.error("Could not serialize to json", e);
        }
        return assignmentJson;
    }


}
