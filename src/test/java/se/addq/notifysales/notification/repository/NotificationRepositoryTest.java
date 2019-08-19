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

    private static final String LONG_MESSAGE = "Hans Svensson Johansson uppdrag Testledare väldigt agilt" +
            " - Avtalet är tillsvidare med två månaders uppsägning på Trafikförsäkringsföreningen har avslutsdatum" +
            "`2019-09-30T00:00`\nTeam ADDQ 2 Continuous\nAnsvarig Nils-Sven Högsten <@U02GPV07G>\nSäljansvarig Nils-Sven Högsten";

    @Before
    public void setup() {
        notificationRepository = new NotificationRepository(notificationDataJpaRepository);
    }

    @Test
    public void saveNotificationData() {
        NotificationRepoData notificationRepoData = getNotificationRepoData();
        notificationRepoData.setMessage(LONG_MESSAGE);
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

    @Test
    public void deleteAllNotificationData() {
        notificationRepository.deleteNotifications();
        verify(notificationDataJpaRepository, times(1)).deleteAll();
    }

    private NotificationRepoData getNotificationRepoData() {
        NotificationRepoData notificationRepoData = new NotificationRepoData();
        notificationRepoData.setId(1L);
        notificationRepoData.setAssignmentId(123);
        return notificationRepoData;
    }
}