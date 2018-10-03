package se.addq.notifysales.configuration;


import com.zaxxer.hikari.HikariDataSource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;
import se.addq.notifysales.notification.repository.MissingDataRepository;
import se.addq.notifysales.notification.repository.MissingNotificationDataJpaRepository;
import se.addq.notifysales.notification.repository.NotificationDataJpaRepository;
import se.addq.notifysales.notification.repository.NotificationRepository;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class DatabaseConfigContextTest {

    @Mock
    MissingNotificationDataJpaRepository missingNotificationDataJpaRepository;

    @Mock
    NotificationDataJpaRepository notificationDataJpaRepository;

    private DatabaseConfig databaseConfig;

    @Before
    public void setUp() {
        databaseConfig = new DatabaseConfig();
    }


    @Test
    public void checkMissingDataHandlerIsCreated() {

        MissingDataRepository missingDataRepository = databaseConfig.missingDataHandlerInit(missingNotificationDataJpaRepository);
        assertThat(missingDataRepository).isNotNull();

    }

    @Test
    public void checkNotificationHandlerIsCreated() {
        NotificationRepository notificationRepository = databaseConfig.notificationHandlerInit(notificationDataJpaRepository);
        assertThat(notificationRepository).isNotNull();
    }

    @Test
    public void checkDataSourceIsCreatedAndClosed() {
        HikariDataSource hikariDataSource = databaseConfig.dataSource();
        assertThat(hikariDataSource).isNotNull();
        databaseConfig.close();
    }

}
