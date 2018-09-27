package se.addq.notifysales.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import se.addq.notifysales.notification.model.MissingNotificationData;
import se.addq.notifysales.notification.model.NotificationData;
import se.addq.notifysales.slack.SlackApi;
import se.addq.notifysales.utils.SleepUtil;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Component
class NotificationPushTask {

    @Value("${slack.notification.missingdata.slackid}")
    private String slackIdForUserToNotifyWhenDataMissing;

    private final SlackApi slackApi;

    private final NotificationService notificationService;

    private static final int POLLING_INTERVAL_MS = 5 * 60 * 1000;
    private static final int POLLING_INTERVAL_DELAY_MS = 2 * 60 * 1000;

    private static final int POLLING_INTERVAL_MISSING_DATA_MS = 60 * 60 * 1000;
    private static final int POLLING_INTERVAL_MISSING_DATA_DELAY_MS = 3 * 60 * 1000;

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    NotificationPushTask(SlackApi slackApi, NotificationService notificationService) {
        this.slackApi = slackApi;
        this.notificationService = notificationService;
    }

    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    @Scheduled(fixedRate = POLLING_INTERVAL_MS, initialDelay = POLLING_INTERVAL_DELAY_MS)
    void notifyAboutAssignmentsEnding() {
        log.info("Notify slack about assignments ending -> interval {} seconds", POLLING_INTERVAL_MS / 1000);
        final List<NotificationData> assignmentsToNotify = notificationService.getAssignmentsToNotify();
        if (assignmentsToNotify.isEmpty()) {
            log.info("No new assignment to notify!");
        } else {
            synchronized (assignmentsToNotify) {
                for (NotificationData notificationData : assignmentsToNotify) {
                    if (notificationData.isReadyToBeNotified()) {
                        log.info("Assignment ready to notify {}", notificationData);
                        SleepUtil.sleepMilliSeconds(500);
                        String message = NotificationMessageCreator.getMessageForNotificationEndingAssignment(notificationData);
                        if (slackApi.sendNotification(message)) {
                            notificationService.persistAssignmentNotified(notificationData, message);
                        } else {
                            log.warn("Could not send message will re-try later!");
                        }
                    }
                }
                notificationService.clearAssignmentsToNotify();
            }
        }
    }

    @Scheduled(fixedRate = POLLING_INTERVAL_MISSING_DATA_MS, initialDelay = POLLING_INTERVAL_MISSING_DATA_DELAY_MS)
    void notifyAboutAssignmentsWithMissingData() {
        log.info("Notify slack about assignments missing data  -> interval {} hours", POLLING_INTERVAL_MISSING_DATA_MS / 60 * 1000);
        List<MissingNotificationData> missingDataForAssignments = notificationService.getMissingDataForAssignments();
        if (missingDataForAssignments.isEmpty()) {
            log.info("No missing data assignments to notify about!");
        } else {
            for (MissingNotificationData missingNotificationData : missingDataForAssignments) {
                if (missingNotificationData.isNotified()) {
                    continue;
                }
                SleepUtil.sleepMilliSeconds(500);
                String message = NotificationMessageCreator.getMessageForMissingNotificationData(missingNotificationData, slackIdForUserToNotifyWhenDataMissing);
                if ("".equals(message)) {
                    log.warn("Can not send / store message due to missing data! {}", missingDataForAssignments);
                    continue;
                }
                if (slackApi.sendNotification(message)) {
                    missingNotificationData.setNotified(true);
                } else {
                    log.warn("Failed to send message to Slack. Will re-try later!");
                }
                notificationService.persistInfoForMissingData(missingNotificationData);
            }
            notificationService.clearMissingDataToNotify();
        }

    }


}