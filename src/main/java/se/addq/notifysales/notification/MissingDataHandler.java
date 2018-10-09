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

    private final List<MissingNotificationData> missingDataToBeNotifiedList = new ArrayList<>();

    private final List<MissingNotificationData> alreadyNotifiedMissingDataList;

    private final MissingDataRepository missingDataRepository;

    @Autowired
    public MissingDataHandler(MissingDataRepository missingDataRepository) {
        this.missingDataRepository = missingDataRepository;
        this.alreadyNotifiedMissingDataList = getPersistedMissingNotificationData();
    }

    boolean isIncompleteDataForNotification(int assignmentId) {
        for (MissingNotificationData missingNotificationData : getAlreadyNotifiedMissingDataList()) {
            if (assignmentId == missingNotificationData.getAssignmentId()) {
                log.debug("AssignmentResponse has not complete data {}", assignmentId);
                return true;
            }
        }
        return false;
    }


    void addMissingData(NotificationData notificationData, MissingDataType missingDataType, String data) {
        if (isAlreadyInNotifiedMissingDataList(notificationData.getAssignmentId())) {
            log.info("Missing data already in list");
            return;
        }
        log.info("Found new missing data will add to list");
        MissingNotificationData missingNotificationData = new MissingNotificationData();
        missingNotificationData.setAssignmentId(notificationData.getAssignmentId());
        missingNotificationData.setMissingdataType(missingDataType);
        missingNotificationData.setMissingData(data);
        missingDataToBeNotifiedList.add(missingNotificationData);
    }

    public List<MissingNotificationData> getMissingDataReadyToBeNotifiedList() {
        return missingDataToBeNotifiedList;
    }

    public void clearMissingDataNotifiedList(List<MissingNotificationData> missingNotificationDataList) {
        log.info("Will clear missing data assignments already notified");
        missingDataToBeNotifiedList.removeAll(missingNotificationDataList);
    }

    List<MissingNotificationData> getAlreadyNotifiedMissingDataList() {
        return alreadyNotifiedMissingDataList;
    }

    public void addAlreadyNotifiedMissingData(MissingNotificationData missingNotificationData) {
        alreadyNotifiedMissingDataList.add(missingNotificationData);
    }


    public void persistMissingDataNotifications(MissingNotificationData missingNotificationData) {
        log.info("Save info about missing notification data to storage");
        missingDataRepository.saveMissingNotificationData(missingNotificationData);
    }


    void removeFromMissingDataIfExisting(int assignmentId) {
        MissingNotificationData missingNotificationData = alreadyNotifiedMissingDataList.stream().filter(missingData -> missingData.getAssignmentId() == assignmentId).findFirst().orElse(null);
        if (missingNotificationData != null) {
            log.info("Will remove fixed data from notified missing data list {}", missingNotificationData);
            removeMissingNotificationDataFromDb(assignmentId);
            alreadyNotifiedMissingDataList.remove(missingNotificationData);
        }
    }


    private boolean isAlreadyInNotifiedMissingDataList(int assignmentId) {
        return getAlreadyNotifiedMissingDataList().stream().anyMatch(missing -> missing.getAssignmentId() == assignmentId);
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


}
