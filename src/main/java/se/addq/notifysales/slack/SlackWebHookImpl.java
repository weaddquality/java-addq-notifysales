package se.addq.notifysales.slack;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.lang.invoke.MethodHandles;

@Component
public class SlackWebHookImpl implements SlackApi {

    private static final String OK_RESPONSE_FROM_SLACK = "ok";
    @Value("${slack.webhook.url}")
    private String slackWebhookUrl;

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final RestTemplate restTemplate;

    @Autowired
    SlackWebHookImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean sendNotification(String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        SlackNotification slackNotification = new SlackNotification();
        slackNotification.setText(message);
        String slackMessageJson = "";
        try {
            slackMessageJson =
                    new ObjectMapper().writeValueAsString(slackNotification);
            log.debug("body {}", slackMessageJson);
        } catch (JsonProcessingException e) {
            log.error("Could not serialize to json", e);
        }
        HttpEntity<Object> entity = new HttpEntity<>(slackMessageJson, headers);
        String resp = restTemplate.postForObject(slackWebhookUrl, entity, String.class);
        if (OK_RESPONSE_FROM_SLACK.equals(resp)) {
            log.info("Successfully sent notification to Slack with message {}", slackMessageJson);
            return true;
        } else {
            log.error("Could not send notification to Slack!");
            return false;
        }
    }
}
