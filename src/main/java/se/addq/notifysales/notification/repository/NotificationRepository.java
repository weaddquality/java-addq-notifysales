package se.addq.notifysales.notification.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import se.addq.notifysales.notification.model.NotificationRepoData;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

public class NotificationRepository {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final NotificationDataJpaRepository notificationDataJpaRepository;

    @Autowired
    public NotificationRepository(NotificationDataJpaRepository notificationDataJpaRepository) {
        this.notificationDataJpaRepository = notificationDataJpaRepository;
    }

    public void saveNotificationData(NotificationRepoData notificationRepoData) {
        notificationDataJpaRepository.save(notificationRepoData);
    }

    public void deleteNotifications() {
        log.info("Will delete all notifications in DB");
        notificationDataJpaRepository.deleteAll();
    }


    public List<NotificationRepoData> findAllNotificationData() {
        log.info("Get saved notification data from storage");
        List<NotificationRepoData> notificationDataList = new ArrayList<>();
        Iterable<NotificationRepoData> notificationDataIterable = notificationDataJpaRepository.findAll();
        notificationDataIterable.forEach(notificationDataList::add);
        log.info("Got {} notified data items from DB", notificationDataList.size());
        return notificationDataList;
    }
}
