package se.addq.notifysales.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
class AssignmentPollTask {

    private static final int POLLING_INTERVAL_DELAY_MS = 30000;

    private static final int POLLING_INTERVAL_MS = 60 * 1000;

    private final NotificationServiceApi notificationServiceApi;

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    AssignmentPollTask(NotificationServiceApi notificationServiceApi) {
        this.notificationServiceApi = notificationServiceApi;
    }

    @Scheduled(fixedRate = POLLING_INTERVAL_MS, initialDelay = POLLING_INTERVAL_DELAY_MS)
    void updateAssignmentListToNotifyInBatch() {
        log.info("Poll assignments from Cinode -> interval {} seconds", POLLING_INTERVAL_MS / 1000);
        notificationServiceApi.updateAssignmentsToNotify();
    }

}