package se.addq.notifysales.notification.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import se.addq.notifysales.notification.model.MissingNotificationData;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

public class MissingDataRepository {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final MissingNotificationDataJpaRepository missingNotificationDataJpaRepository;

    @Autowired
    public MissingDataRepository(MissingNotificationDataJpaRepository missingNotificationDataJpaRepository) {
        this.missingNotificationDataJpaRepository = missingNotificationDataJpaRepository;
    }

    public void saveMissingNotificationData(MissingNotificationData missingNotificationData) {
        missingNotificationDataJpaRepository.save(missingNotificationData);
    }

    public List<MissingNotificationData> findAllNotificationData() {
        log.info("Get saved notification data from storage");
        List<MissingNotificationData> notificationDataList = new ArrayList<>();
        Iterable<MissingNotificationData> notificationDataIterable = missingNotificationDataJpaRepository.findAll();
        notificationDataIterable.forEach(notificationDataList::add);
        log.info("Got {} notified data items from DB", notificationDataList.size());
        return notificationDataList;
    }

    public void delete(MissingNotificationData missingNotificationData) {
        missingNotificationDataJpaRepository.delete(missingNotificationData);
    }


    public MissingNotificationData findByAssignmentId(int assignmentId) {
        return missingNotificationDataJpaRepository.findByAssignmentId(assignmentId);
    }
}
