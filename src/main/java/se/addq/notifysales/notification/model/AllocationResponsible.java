package se.addq.notifysales.notification.model;

import java.io.Serializable;

public class AllocationResponsible implements Serializable {

    private String name = "";

    private String email = "";

    private String slackChannel = "";

    private String slackUserId = "";

    private int teamId = 0;

    private String teamName = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSlackChannel() {
        return slackChannel;
    }

    public void setSlackChannel(String slackChannel) {
        this.slackChannel = slackChannel;
    }

    public String getSlackUserId() {
        return slackUserId;
    }

    public void setSlackUserId(String slackUserId) {
        this.slackUserId = slackUserId;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    @Override
    public String toString() {
        return "AllocationResponsible{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", slackChannel='" + slackChannel + '\'' +
                ", slackUserId='" + slackUserId + '\'' +
                ", teamId=" + teamId +
                ", teamName='" + teamName + '\'' +
                '}';
    }
}


