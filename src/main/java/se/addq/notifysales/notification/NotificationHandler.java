package se.addq.notifysales.notification;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import se.addq.notifysales.notification.model.NotificationData;
import se.addq.notifysales.notification.model.NotificationRepoData;
import se.addq.notifysales.notification.repository.NotificationDataJpaRepository;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class NotificationHandler {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final List<NotificationRepoData> notificationRepoDataList;

    private final NotificationDataJpaRepository notificationDataJpaRepository;

    private final List<NotificationData> assignmentsToNotify = Collections.synchronizedList(new ArrayList<>());

    private final List<NotificationData> incompleteAssignmentList = new ArrayList<>();


    @Autowired
    public NotificationHandler(NotificationDataJpaRepository notificationDataJpaRepository) {
        this.notificationDataJpaRepository = notificationDataJpaRepository;
        this.notificationRepoDataList = getPersistedNotifiedAssignments();
    }

    void addAndPersistNotificationStatus(NotificationData notificationData, String message) {
        log.info("Save notification data to storage");
        NotificationRepoData notificationRepoData = new NotificationRepoData();
        notificationRepoData.setAssignmentId(notificationData.getAssignmentId());
        notificationRepoData.setNotifiedTime(LocalDateTime.now());
        notificationRepoData.setMessage(message);
        notificationDataJpaRepository.save(notificationRepoData);
        notificationRepoDataList.add(notificationRepoData);
    }

    private List<NotificationRepoData> getPersistedNotifiedAssignments() {
        log.info("Get saved notification data from storage");
        List<NotificationRepoData> notificationDataList = new ArrayList<>();
        Iterable<NotificationRepoData> notificationDataIterable = notificationDataJpaRepository.findAll();
        notificationDataIterable.forEach(notificationDataList::add);
        log.info("Got {} notified data items from DB", notificationDataList.size());
        return notificationDataList;
    }


    List<NotificationRepoData> getAlreadyNotifiedAssignments() {
        return notificationRepoDataList;
    }

    List<NotificationData> getAssignmentsToNotify() {
        return assignmentsToNotify;
    }

    void removeNotCompleteAssignments() {
        assignmentsToNotify.removeAll(incompleteAssignmentList);
    }


    void clearAssignmentsToNotify() {
        log.info("Will clear assignments notified");
        assignmentsToNotify.clear();
    }

    void incompleteAssignmentAdd(NotificationData notificationData) {
        incompleteAssignmentList.add(notificationData);
    }

    void assignmentsToNotifyAdd(NotificationData notificationData) {
        incompleteAssignmentList.add(notificationData);
    }
}
