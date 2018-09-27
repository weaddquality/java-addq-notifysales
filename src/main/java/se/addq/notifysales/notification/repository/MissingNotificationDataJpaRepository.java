package se.addq.notifysales.notification.repository;

import org.springframework.data.repository.CrudRepository;
import se.addq.notifysales.notification.model.MissingNotificationData;


public interface MissingNotificationDataJpaRepository extends CrudRepository<MissingNotificationData, Long> {
    MissingNotificationData findByAssignmentId(int assignmentId);

}


