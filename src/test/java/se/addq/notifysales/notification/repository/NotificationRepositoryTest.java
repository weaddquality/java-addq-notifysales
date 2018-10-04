package se.addq.notifysales.notification.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;
import se.addq.notifysales.notification.model.NotificationRepoData;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
public class NotificationRepositoryTest {

    @Mock
    private NotificationDataJpaRepository notificationDataJpaRepository;

    private NotificationRepository notificationRepository;

    @Before
    public void setup() {
        notificationRepository = new NotificationRepository(notificationDataJpaRepository);
    }

    @Test
    public void saveNotificationData() {
        NotificationRepoData notificationRepoData = getNotificationRepoData();
        notificationRepository.saveNotificationData(notificationRepoData);
        verify(notificationDataJpaRepository, times(1)).save(Mockito.any());
    }


    @Test
    public void findAllNotificationData() {
        List<NotificationRepoData> notificationRepoTestDataList = new ArrayList<>();
        notificationRepoTestDataList.add(getNotificationRepoData());
        Mockito.when(notificationDataJpaRepository.findAll()).thenReturn(notificationRepoTestDataList);
        List<NotificationRepoData> notificationRepoDataList = notificationRepository.findAllNotificationData();
        assertThat(notificationRepoDataList.size()).isOne();
    }

    private NotificationRepoData getNotificationRepoData() {
        NotificationRepoData notificationRepoData = new NotificationRepoData();
        notificationRepoData.setId(1L);
        notificationRepoData.setAssignmentId(123);
        return notificationRepoData;
    }
}