package se.addq.notifysales.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.addq.notifysales.notification.model.MissingDataType;
import se.addq.notifysales.notification.model.MissingNotificationData;
import se.addq.notifysales.notification.model.NotificationData;
import se.addq.notifysales.notification.repository.MissingDataRepository;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Component
public class MissingDataHandler {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final List<MissingNotificationData> missingNotificationDataList;

    private final MissingDataRepository missingDataRepository;

    @Autowired
    public MissingDataHandler(MissingDataRepository missingDataRepository) {
        this.missingDataRepository = missingDataRepository;
        this.missingNotificationDataList = getPersistedMissingNotificationData();
    }

    boolean isIncompleteDataForNotification(int assignmentId) {
        for (MissingNotificationData missingNotificationData : getMissingNotificationDataList()) {
            if (assignmentId == missingNotificationData.getAssignmentId()) {
                log.debug("AssignmentResponse has not complete data {}", assignmentId);
                return true;
            }
        }
        return false;
    }


    void addTeamIsMissingForUser(NotificationData notificationData) {
        MissingNotificationData missingNotificationData = new MissingNotificationData();
        missingNotificationData.setAssignmentId(notificationData.getAssignmentId());
        missingNotificationData.setMissingdataType(MissingDataType.MISSING_TEAM_FOR_USER);
        missingNotificationData.setMissingData(notificationData.getAssignmentConsultant().toString());
        missingNotificationDataList.add(missingNotificationData);
    }

    void addAllocationResponsibleIsMissingForTeam(NotificationData notificationData, String teamName) {
        MissingNotificationData missingNotificationData = new MissingNotificationData();
        missingNotificationData.setAssignmentId(notificationData.getAssignmentId());
        missingNotificationData.setMissingdataType(MissingDataType.MISSING_ALLOCATION_RESPONSIBLE);
        missingNotificationData.setMissingData(teamName);
        missingNotificationDataList.add(missingNotificationData);
    }

    void addMissingAssignedForAssignment(NotificationData notificationData, String reason) {
        MissingNotificationData missingNotificationData = new MissingNotificationData();
        missingNotificationData.setAssignmentId(notificationData.getAssignmentId());
        missingNotificationData.setMissingdataType(MissingDataType.MISSING_ASSIGNED);
        missingNotificationData.setMissingData(reason);
        missingNotificationDataList.add(missingNotificationData);
    }


    void removeFromMissingDataIfExisting(int assignmentId) {
        for (MissingNotificationData missingNotificationData : missingNotificationDataList) {
            if (missingNotificationData.getAssignmentId() == assignmentId) {
                removeMissingNotificationDataFromDb(assignmentId);
            }
        }
    }

    List<MissingNotificationData> getMissingNotificationDataList() {
        return missingNotificationDataList;
    }

    void persistMissingDataNotifications(MissingNotificationData missingNotificationData) {
        log.info("Save info about missing notification data to storage");
        missingDataRepository.saveMissingNotificationData(missingNotificationData);
    }

    private List<MissingNotificationData> getPersistedMissingNotificationData() {
        log.info("Get saved missing notification data from storage");
        List<MissingNotificationData> notificationDataList = new ArrayList<>();
        Iterable<MissingNotificationData> notificationDataIterable = missingDataRepository.findAllNotificationData();
        notificationDataIterable.forEach(notificationDataList::add);
        log.info("Got {} missing data items from DB", notificationDataList.size());
        return notificationDataList;
    }

    private void removeMissingNotificationDataFromDb(int assignmentId) {
        MissingNotificationData missingNotificationData = missingDataRepository.findByAssignmentId(assignmentId);
        log.info("Will delete Missing Notification Data item {}", missingNotificationData);
        missingDataRepository.delete(missingNotificationData);
    }


    void clearAssignmentsToNotify() {
        log.info("Will clear missing data assignments notified");
        missingNotificationDataList.clear();
    }


}
