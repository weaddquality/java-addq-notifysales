package se.addq.notifysales.notification.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import se.addq.notifysales.notification.NotificationServiceApi;

import java.lang.invoke.MethodHandles;

@Component
class AssignmentPollTask {

    private static final String TIMEZONE_EUROPE_STOCKHOLM = "Europe/Stockholm";

    private static final int POLLING_INTERVAL_DELAY_MS = 30000;

    private static final int POLLING_INTERVAL_MS = 60 * 1000;

    private final NotificationServiceApi notificationServiceApi;

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    // The cron pattern is a list of six single space-separated fields:
    // representing second, minute, hour, day, month, weekday.
    // Month and weekday names can be given as the first three letters of the English names.
    // Run every thursday at 9 example, 0 0 9 * * THU

    private final static String cronPropertyName = "${cinode.poll.cron}";

    @Value(cronPropertyName)
    private String cronValue;

    @Autowired
    AssignmentPollTask(NotificationServiceApi notificationServiceApi) {
        this.notificationServiceApi = notificationServiceApi;
    }

    @Scheduled(cron = cronPropertyName, zone = TIMEZONE_EUROPE_STOCKHOLM)
    void updateAssignmentListToNotifyInBatch() {
        log.info("Poll assignments from Cinode -> interval cron:'{}'", cronValue);
        notificationServiceApi.updateAssignmentsToNotify();
    }

}