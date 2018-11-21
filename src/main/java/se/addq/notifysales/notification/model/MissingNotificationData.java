package se.addq.notifysales.notification.model;

import javax.persistence.*;

@Entity
public class MissingNotificationData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private boolean isNotified;

    private int assignmentId;

    @Enumerated(EnumType.STRING)
    private MissingDataType MissingdataType;

    private String missingData;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isNotified() {
        return isNotified;
    }

    public void setNotified(boolean notified) {
        isNotified = notified;
    }

    public int getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(int assignmentId) {
        this.assignmentId = assignmentId;
    }

    public MissingDataType getMissingdataType() {
        return MissingdataType;
    }

    public void setMissingdataType(MissingDataType missingdataType) {
        MissingdataType = missingdataType;
    }

    public String getMissingData() {
        return missingData;
    }

    public void setMissingData(String missingData) {
        this.missingData = missingData;
    }

    @Override
    public String toString() {
        return "MissingNotificationData{" +
                "id=" + id +
                ", isNotified=" + isNotified +
                ", assignmentId=" + assignmentId +
                ", MissingdataType=" + MissingdataType +
                ", missingData='" + missingData + '\'' +
                '}';
    }
}
