package se.addq.notifysales.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import se.addq.notifysales.slack.SlackApi;

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = "/notification")
class NotificationController {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private SlackApi slackApi;

    private NotificationServiceApi notificationService;

    @Autowired
    public NotificationController(NotificationServiceApi notificationService, SlackApi slackApi) {
        this.notificationService = notificationService;
        this.slackApi = slackApi;
    }


    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public String index() {
        return "Notify Service is up an running!";
    }


    //TODO:Re-add this when when API is secured
    /*
    @RequestMapping(value = "/download/allocation/config/used", method = RequestMethod.GET)
    public ResponseEntity<byte[]> downloadAllocationUsedAsCsv() {
        byte[] csvFileByteArray = notificationService.getAllocationResponsibleConfiguration();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "allokeringsansvariga-per-team-mall" + ".csv" + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvFileByteArray);
    }


    @RequestMapping(value = "/notifiedlist", method = RequestMethod.GET)
    public String getNotifiedList() {
        log.info("Fetch list of notified assignments");
        String notifiedAssignmentsAsJson = notificationService.getListOfNotifiedAssignments();
        log.debug("Got list {}", notifiedAssignmentsAsJson);
        return notifiedAssignmentsAsJson;
    }

    @RequestMapping(value = "/slack/test", method = RequestMethod.POST)
    public String slack() {
        String message = "Hello Channel" + System.currentTimeMillis();
        if (slackApi.sendNotification(message)) {
            return "Successfully sent message {} to Slack!";
        }
        return "Failed to send message {} to Slack";
    }
    */

}
