package se.addq.notifysales.notification.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import se.addq.notifysales.notification.model.NotificationRepoData;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class NotificationDataJpaRepositoryTest {

    @Autowired
    private NotificationDataJpaRepository notificationDataJpaRepository;

    private LocalDateTime localDateTime;

    @Before
    public void setUp() {
        LocalDateTime localDateTime = LocalDateTime.now();
        NotificationRepoData notificationRepoData = new NotificationRepoData();
        notificationRepoData.setAssignmentId(123);
        notificationRepoData.setNotifiedTime(localDateTime);
        assertThat(notificationRepoData.getId()).isNull();
        this.notificationDataJpaRepository.save(notificationRepoData);
        assertThat(notificationRepoData.getId()).isNotNull();
    }

    @Test
    public void findAllWillReturnAllNotificationRepoDataInNotificationDb() {
        Iterable<NotificationRepoData> notificationDataIterable = notificationDataJpaRepository.findAll();
        int count = 0;
        for (NotificationRepoData notificationRepoData : notificationDataIterable) {
            count++;
        }
        assertThat(count).isEqualTo(1);
    }

    @After
    public void tearDown() {
        this.notificationDataJpaRepository.deleteAll();
    }

}






