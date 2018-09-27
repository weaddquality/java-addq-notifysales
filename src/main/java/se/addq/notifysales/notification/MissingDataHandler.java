package se.addq.notifysales.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import se.addq.notifysales.notification.model.MissingDataType;
import se.addq.notifysales.notification.model.MissingNotificationData;
import se.addq.notifysales.notification.model.NotificationData;
import se.addq.notifysales.notification.repository.MissingNotificationDataJpaRepository;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;


public class MissingDataHandler {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final List<MissingNotificationData> missingNotificationDataList;

    private final MissingNotificationDataJpaRepository missingNotificationDataJpaRepository;

    @Autowired
    public MissingDataHandler(MissingNotificationDataJpaRepository missingNotificationDataJpaRepository) {
        this.missingNotificationDataJpaRepository = missingNotificationDataJpaRepository;
        this.missingNotificationDataList = getPersistedMissingNotificationData();
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
        missingNotificationDataJpaRepository.save(missingNotificationData);
    }

    private List<MissingNotificationData> getPersistedMissingNotificationData() {
        log.info("Get saved missing notification data from storage");
        List<MissingNotificationData> notificationDataList = new ArrayList<>();
        Iterable<MissingNotificationData> notificationDataIterable = missingNotificationDataJpaRepository.findAll();
        notificationDataIterable.forEach(notificationDataList::add);
        log.info("Got {} missing data items from DB", notificationDataList.size());
        return notificationDataList;
    }

    private void removeMissingNotificationDataFromDb(int assignmentId) {
        MissingNotificationData missingNotificationData = missingNotificationDataJpaRepository.findByAssignmentId(assignmentId);
        log.info("Will delete Missing Notification Data item {}", missingNotificationData);
        missingNotificationDataJpaRepository.delete(missingNotificationData);
    }


    void clearAssignmentsToNotify() {
        missingNotificationDataList.clear();
    }
}
