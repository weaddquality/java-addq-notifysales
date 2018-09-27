package se.addq.notifysales.notification.repository;

import org.springframework.data.repository.CrudRepository;
import se.addq.notifysales.notification.model.NotificationData;
import se.addq.notifysales.notification.model.NotificationRepoData;

import java.util.List;


public interface NotificationDataJpaRepository extends CrudRepository<NotificationRepoData, Long> {
    List<NotificationData> findByAssignmentId(String assignmentId);

}


