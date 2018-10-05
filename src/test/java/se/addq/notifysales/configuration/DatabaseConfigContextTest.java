package se.addq.notifysales.configuration;


import com.zaxxer.hikari.HikariDataSource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;
import se.addq.notifysales.notification.repository.*;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class DatabaseConfigContextTest {

    @Mock
    MissingNotificationDataJpaRepository missingNotificationDataJpaRepository;

    @Mock
    NotificationDataJpaRepository notificationDataJpaRepository;

    @Mock
    AllocationResponsibleJpaRepository allocationResponsibleJpaRepository;

    private DatabaseConfig databaseConfig;

    @Before
    public void setUp() {
        databaseConfig = new DatabaseConfig();
    }


    @Test
    public void checkMissingDataRepositoryIsCreated() {

        MissingDataRepository missingDataRepository = databaseConfig.missingDataRepositoryInit(missingNotificationDataJpaRepository);
        assertThat(missingDataRepository).isNotNull();

    }

    @Test
    public void checkNotificationRepositoryIsCreated() {
        NotificationRepository notificationRepository = databaseConfig.notificationRepositoryInit(notificationDataJpaRepository);
        assertThat(notificationRepository).isNotNull();
    }


    @Test
    public void checkAllocationResponsibleRepositoryIsCreated() {
        AllocationResponsibleDataRepository allocationResponsibleDataRepository = databaseConfig.allocationResponsibleDataRepositoryInit(allocationResponsibleJpaRepository);
        assertThat(allocationResponsibleDataRepository).isNotNull();
    }

    @Test
    public void checkDataSourceIsCreatedAndClosed() {
        HikariDataSource hikariDataSource = databaseConfig.dataSource();
        assertThat(hikariDataSource).isNotNull();
        databaseConfig.close();
    }

}
