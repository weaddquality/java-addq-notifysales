package se.addq.notifysales.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.addq.notifysales.notification.model.MissingNotificationData;
import se.addq.notifysales.notification.model.NotificationData;

import java.lang.invoke.MethodHandles;

public class NotificationMessageCreator {

    private static final String MESSAGE_FOR_MISSING_TEAM = "Vi saknar data i Cinode för vilket Team användare %s tillhör och kan inte skicka notifiering till Slack %s";
    private static final String MESSAGE_FOR_MISSING_ALLOCATION_RESPONSIBLE = "Vi saknar data för allokeringsansvarig för Team:'%s' och kan inte skicka notifiering till Slack %s";
    private static final String MESSAGE_FOR_MISSING_ASSIGNED = "Vi saknar data för ansvarig konsult för uppdrag:'%s' och kan inte skicka notifiering till Slack %s";

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static String getMessageForNotificationEndingAssignment(NotificationData notificationData) {
        String responsible = getResponsibleAsSlackUserIdOrTextIfMissing(notificationData);
        return String.format("%s %s uppdrag %s på %s har " +
                        "avslutsdatum %s Team %s Ansvarig %s",
                notificationData.getAssignmentConsultant().getFirstName(),
                notificationData.getAssignmentConsultant().getLastName(),
                notificationData.getAssignmentTitle(),
                notificationData.getAssignmentCustomer().getName(),
                notificationData.getEndDate(),
                notificationData.getAllocationResponsible().getTeamName(),
                responsible);
    }

    private static String getResponsibleAsSlackUserIdOrTextIfMissing(NotificationData notificationData) {
        if (notificationData.getAllocationResponsible().getSlackUserId() == null || notificationData.getAllocationResponsible().getSlackUserId().equals("")) {
            return notificationData.getAllocationResponsible().getName();
        }
        return notificationData.getAllocationResponsible().getName() + " " + getSlackFormattedSlackId(notificationData.getAllocationResponsible().getSlackUserId());
    }

    private static String getSlackFormattedSlackId(String slackId) {
        return "<@" + slackId + ">";
    }

    public static String getMessageForMissingNotificationData(MissingNotificationData missingNotificationData, String slackId) {

        switch (missingNotificationData.getMissingdataType()) {
            case MISSING_TEAM_FOR_USER:
                return String.format(MESSAGE_FOR_MISSING_TEAM,
                        missingNotificationData.getMissingData(),
                        getSlackFormattedSlackId(slackId));
            case MISSING_ALLOCATION_RESPONSIBLE:
                return String.format(MESSAGE_FOR_MISSING_ALLOCATION_RESPONSIBLE,
                        missingNotificationData.getMissingData(),
                        getSlackFormattedSlackId(slackId));
            case MISSING_ASSIGNED:
                return String.format(MESSAGE_FOR_MISSING_ASSIGNED, missingNotificationData.getMissingData(), getSlackFormattedSlackId(slackId));
            default:
                log.warn("Missing data type for message to be created! {}", missingNotificationData.getMissingdataType());
                return "";

        }
    }

}
