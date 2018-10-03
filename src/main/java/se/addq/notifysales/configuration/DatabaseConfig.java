package se.addq.notifysales.configuration;


import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import se.addq.notifysales.notification.repository.MissingDataRepository;
import se.addq.notifysales.notification.repository.MissingNotificationDataJpaRepository;
import se.addq.notifysales.notification.repository.NotificationDataJpaRepository;
import se.addq.notifysales.notification.repository.NotificationRepository;

import javax.annotation.PreDestroy;

@Configuration
@EnableJpaRepositories(basePackages = "se.addq.notifysales")
@EnableTransactionManagement
class DatabaseConfig {

    private HikariDataSource hikariDataSource;


    @Bean(name = "notificationRepository")
    NotificationRepository notificationHandlerInit(NotificationDataJpaRepository notificationDataJpaRepository) {
        return new NotificationRepository(notificationDataJpaRepository);
    }

    @Bean(name = "missingDataRepository")
    MissingDataRepository missingDataHandlerInit(MissingNotificationDataJpaRepository missingNotificationDataJpaRepository) {
        return new MissingDataRepository(missingNotificationDataJpaRepository);
    }

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    HikariDataSource dataSource() {
        hikariDataSource = DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();

        return hikariDataSource;
    }

    @PreDestroy
    void close() {
        hikariDataSource.close();
    }
}
