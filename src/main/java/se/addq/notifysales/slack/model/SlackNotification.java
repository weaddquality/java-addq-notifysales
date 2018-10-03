package se.addq.notifysales.slack.model;

import org.springframework.boot.jackson.JsonComponent;

@JsonComponent
public class SlackNotification {

    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


}
