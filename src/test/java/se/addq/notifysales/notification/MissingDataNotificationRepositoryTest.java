package se.addq.notifysales.notification;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import se.addq.notifysales.notification.model.MissingNotificationData;
import se.addq.notifysales.notification.repository.MissingNotificationDataJpaRepository;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class MissingDataNotificationRepositoryTest {

    @Autowired
    private MissingNotificationDataJpaRepository missingNotificationDataJpaRepository;

    @Before
    public void setUp() {
        MissingNotificationData notificationRepoData = new MissingNotificationData();
        notificationRepoData.setAssignmentId(123);
        assertThat(notificationRepoData.getId()).isNull();
        this.missingNotificationDataJpaRepository.save(notificationRepoData);
        assertThat(notificationRepoData.getId()).isNotNull();
    }

    @Test
    public void findAllWillReturnAllNotificationRepoDataInNotificationDb() {
        Iterable<MissingNotificationData> missingNotificationDataIterable = missingNotificationDataJpaRepository.findAll();
        assertThat(missingNotificationDataIterable.spliterator().getExactSizeIfKnown()).isEqualTo(1);
    }

    @After
    public void tearDown() {
        this.missingNotificationDataJpaRepository.deleteAll();
    }

}






