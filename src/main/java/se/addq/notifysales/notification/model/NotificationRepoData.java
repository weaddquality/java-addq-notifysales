package se.addq.notifysales.notification.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class NotificationRepoData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDateTime notifiedTime;

    private int assignmentId;

    private String message;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getNotifiedTime() {
        return notifiedTime;
    }

    public void setNotifiedTime(LocalDateTime notifiedTime) {
        this.notifiedTime = notifiedTime;
    }

    public int getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(int assignmentId) {
        this.assignmentId = assignmentId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "NotificationRepoData{" +
                "id=" + id +
                ", notifiedTime=" + notifiedTime +
                ", assignmentId=" + assignmentId +
                ", message='" + message + '\'' +
                '}';
    }
}
