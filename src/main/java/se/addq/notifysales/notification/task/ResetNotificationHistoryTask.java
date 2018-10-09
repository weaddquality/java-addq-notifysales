package se.addq.notifysales.notification.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import se.addq.notifysales.notification.NotificationHandler;

import java.lang.invoke.MethodHandles;

@Component
class ResetNotificationHistoryTask {

    private static final String TIMEZONE_EUROPE_STOCKHOLM = "Europe/Stockholm";
    private final NotificationHandler notificationHandler;


    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    // The cron pattern is a list of six single space-separated fields:
    // representing second, minute, hour, day, month, weekday.
    // Month and weekday names can be given as the first three letters of the English names.
    //Run every thursday at 9 example, 0 0 9 * * THU
    private final static String cronPropertyName = "${slack.notification.reset.cron}";

    @Value(cronPropertyName)
    private String cronValue;

    @Autowired
    ResetNotificationHistoryTask(NotificationHandler notificationHandler) {
        this.notificationHandler = notificationHandler;
    }

    @Scheduled(cron = cronPropertyName, zone = TIMEZONE_EUROPE_STOCKHOLM)
    void cleanNotificationSentListAndRemoveFromDb() {
        log.info("Reset of send history for notifications -> interval cron:'{}'", cronValue);
        notificationHandler.clearSendNotificationHistory();

    }


}