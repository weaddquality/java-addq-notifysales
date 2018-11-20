package se.addq.notifysales.notification.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import se.addq.notifysales.notification.MissingDataHandler;
import se.addq.notifysales.notification.NotificationMessageCreator;
import se.addq.notifysales.notification.model.MissingNotificationData;
import se.addq.notifysales.slack.SlackApi;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Component
class MissingDataPushTask {

    @Value("${slack.notification.missingdata.slackid}")
    private String slackIdForUserToNotifyWhenDataMissing;

    @Value("${slack.missing.data.webhook.url}")
    private String slackWebhookUrl;

    private final SlackApi slackApi;

    private final MissingDataHandler missingDataHandler;

    private static final int POLLING_INTERVAL_MISSING_DATA_MS = 60 * 60 * 1000;
    private static final int POLLING_INTERVAL_MISSING_DATA_DELAY_MS = 3 * 60 * 1000;

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    MissingDataPushTask(SlackApi slackApi, MissingDataHandler missingDataHandler) {
        this.slackApi = slackApi;
        this.missingDataHandler = missingDataHandler;
    }

    @Scheduled(fixedRate = POLLING_INTERVAL_MISSING_DATA_MS, initialDelay = POLLING_INTERVAL_MISSING_DATA_DELAY_MS)
    void notifyAboutAssignmentsWithMissingData() {
        log.info("Notify slack about assignments missing data  -> interval {} hours", POLLING_INTERVAL_MISSING_DATA_MS / 60 * 1000);
        List<MissingNotificationData> missingDataForAssignments = missingDataHandler.getMissingDataReadyToBeNotifiedList();
        if (missingDataForAssignments.isEmpty()) {
            log.info("No missing data assignments to notify about!");
        } else {
            List<MissingNotificationData> missingNotificationDataNotifedList = new ArrayList<>();
            for (MissingNotificationData missingNotificationData : missingDataForAssignments) {
                if (missingNotificationData.isNotified()) {
                    continue;
                }
                String message = NotificationMessageCreator.getMessageForMissingNotificationData(missingNotificationData, slackIdForUserToNotifyWhenDataMissing);
                if ("".equals(message)) {
                    log.warn("Can not send / store message due to missing data! {}", missingDataForAssignments);
                    continue;
                }
                if (!slackApi.sendNotification(message, slackWebhookUrl)) {
                    log.warn("Failed to send message to Slack. Will re-try later!");
                    continue;
                }
                missingNotificationData.setNotified(true);
                missingDataHandler.addAlreadyNotifiedMissingData(missingNotificationData);
                missingDataHandler.persistMissingDataNotifications(missingNotificationData);
                missingNotificationDataNotifedList.add(missingNotificationData);
            }
            missingDataHandler.clearMissingDataNotifiedList(missingNotificationDataNotifedList);
        }

    }


}