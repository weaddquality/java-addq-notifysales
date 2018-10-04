package se.addq.notifysales.notification;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import se.addq.notifysales.notification.model.AllocationResponsible;
import se.addq.notifysales.notification.model.MissingDataType;
import se.addq.notifysales.notification.model.MissingNotificationData;
import se.addq.notifysales.notification.model.NotificationData;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class NotificationMessageCreatorTest {

    private final NotificationData notificationData = new NotificationData();

    @Before
    public void setup() {
        notificationData.setStartDate(LocalDateTime.now().minusWeeks(12));
        notificationData.setAssignmentTitle("Super tester");
        notificationData.setProjectId(123);
        AllocationResponsible allocationResponsible = new AllocationResponsible();
        allocationResponsible.setTeamId(111);
        allocationResponsible.setTeamName("Addq99");
        notificationData.setAllocationResponsible(allocationResponsible);
    }

    @Test
    public void slackMessageWillGetUserNameWhenAllocationResponsibleSlackIdIsMissing() {
        String expectedMessage = "null null uppdrag Super tester på null har avslutsdatum null Team Addq99 Ansvarig Nisse";
        AllocationResponsible allocationResponsible = notificationData.getAllocationResponsible();
        allocationResponsible.setName("Nisse");
        allocationResponsible.setSlackUserId("");
        notificationData.setAllocationResponsible(allocationResponsible);
        String actualMessage = NotificationMessageCreator.getMessageForNotificationEndingAssignment(notificationData);
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }


    @Test
    public void slackMessageWillGetFormattedSlackIdWhenAllocationResponsibleSlackIdIsExisting() {
        String expectedMessage = "null null uppdrag Super tester på null har avslutsdatum null Team Addq99 Ansvarig <@U12345>";
        AllocationResponsible allocationResponsible = notificationData.getAllocationResponsible();
        allocationResponsible.setName("Nisse");
        allocationResponsible.setSlackUserId("U12345");
        notificationData.setAllocationResponsible(allocationResponsible);
        String actualMessage = NotificationMessageCreator.getMessageForNotificationEndingAssignment(notificationData);
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    public void slackMessageForMissingTeamData() {
        String expectedMessage = "Vi saknar data i Cinode för vilket Team användare AssignmentConsultant{userId=12627, firstName='Nisse', lastName='Hult', teamName='null', teamId=0} tillhör och kan inte skicka notifiering till Slack <@U12345>";
        MissingNotificationData missingNotificationData = new MissingNotificationData();
        missingNotificationData.setAssignmentId(11111);
        missingNotificationData.setMissingData("AssignmentConsultant{userId=12627, firstName='Nisse', lastName='Hult', teamName='null', teamId=0}");
        missingNotificationData.setMissingdataType(MissingDataType.MISSING_TEAM_FOR_USER);
        String actualMessage = NotificationMessageCreator.getMessageForMissingNotificationData(missingNotificationData, "U12345");
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    public void slackMessageForMissingAllocationResponsible() {
        String expectedMessage = "Vi saknar data för allokeringsansvarig för Team:'ADDQ 2 Krav' och kan inte skicka notifiering till Slack <@U12345>";
        MissingNotificationData missingNotificationData = new MissingNotificationData();
        missingNotificationData.setAssignmentId(11111);
        missingNotificationData.setMissingData("ADDQ 2 Krav");
        missingNotificationData.setMissingdataType(MissingDataType.MISSING_ALLOCATION_RESPONSIBLE);
        String actualMessage = NotificationMessageCreator.getMessageForMissingNotificationData(missingNotificationData, "U12345");
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }


}
