package se.addq.notifysales.slack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import se.addq.notifysales.slack.model.SlackNotification;
import se.addq.notifysales.utils.JsonUtil;
import se.addq.notifysales.utils.SleepUtil;

import java.lang.invoke.MethodHandles;

@Component
public class SlackWebHookImpl implements SlackApi {

    private static final String OK_RESPONSE_FROM_SLACK = "ok";

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final RestTemplate restTemplate;

    @Value("${slack.request.interval.ms}")
    private int slackRequestIntervalInMilliSeconds;

    @Autowired
    SlackWebHookImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean sendNotification(String message, String webHookURL) {
        SleepUtil.sleepMilliSeconds(slackRequestIntervalInMilliSeconds);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        SlackNotification slackNotification = new SlackNotification();
        slackNotification.setText(message);
        String slackMessageJson = JsonUtil.getJsonFromObject(slackNotification, false);
        HttpEntity<Object> entity = new HttpEntity<>(slackMessageJson, headers);
        String resp = restTemplate.postForObject(webHookURL, entity, String.class);
        if (OK_RESPONSE_FROM_SLACK.equals(resp)) {
            log.info("Successfully sent notification to Slack with message {}", slackMessageJson);
            return true;
        } else {
            log.error("Could not send notification to Slack!");
            return false;
        }
    }


}
