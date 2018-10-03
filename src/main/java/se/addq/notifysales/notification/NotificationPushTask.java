package se.addq.notifysales.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import se.addq.notifysales.notification.model.NotificationData;
import se.addq.notifysales.slack.SlackApi;
import se.addq.notifysales.utils.SleepUtil;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Component
class NotificationPushTask {


    private final SlackApi slackApi;

    private final NotificationHandler notificationHandler;

    private static final int POLLING_INTERVAL_MS = 5 * 60 * 1000;
    private static final int POLLING_INTERVAL_DELAY_MS = 2 * 60 * 1000;

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    NotificationPushTask(SlackApi slackApi, NotificationHandler notificationHandler) {
        this.slackApi = slackApi;
        this.notificationHandler = notificationHandler;
    }

    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    @Scheduled(fixedRate = POLLING_INTERVAL_MS, initialDelay = POLLING_INTERVAL_DELAY_MS)
    void notifyAboutAssignmentsEnding() {
        log.info("Notify slack about assignments ending -> interval {} seconds", POLLING_INTERVAL_MS / 1000);
        final List<NotificationData> assignmentsToNotify = notificationHandler.getAssignmentsToNotify();
        if (assignmentsToNotify.isEmpty()) {
            log.info("No new assignment to notify!");
        } else {
            synchronized (assignmentsToNotify) {
                for (NotificationData notificationData : assignmentsToNotify) {
                    if (notificationData.isReadyToBeNotified()) {
                        log.info("AssignmentResponse ready to notify {}", notificationData);
                        SleepUtil.sleepMilliSeconds(500);
                        String message = NotificationMessageCreator.getMessageForNotificationEndingAssignment(notificationData);
                        if (slackApi.sendNotification(message)) {
                            notificationHandler.addAndPersistNotificationStatus(notificationData, message);
                        } else {
                            log.warn("Could not send message will re-try later!");
                        }
                    }
                }
                notificationHandler.clearAssignmentsToNotify();
            }
        }
    }



}