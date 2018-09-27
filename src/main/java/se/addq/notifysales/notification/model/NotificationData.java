package se.addq.notifysales.notification.model;


import java.time.LocalDateTime;


public class NotificationData {

    private boolean isReadyToBeNotified = false;

    private AssignmentCustomer assignmentCustomer = new AssignmentCustomer();

    private int projectId;

    private int assignmentId;

    private String assignmentTitle;

    private AssignmentConsultant assignmentConsultant = new AssignmentConsultant();

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private AllocationResponsible allocationResponsible = new AllocationResponsible();

    public boolean isReadyToBeNotified() {
        return isReadyToBeNotified;
    }

    public void setReadyToBeNotified(boolean readyToBeNotified) {
        isReadyToBeNotified = readyToBeNotified;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(int assignmentId) {
        this.assignmentId = assignmentId;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public AllocationResponsible getAllocationResponsible() {
        return allocationResponsible;
    }

    public AssignmentCustomer getAssignmentCustomer() {
        return assignmentCustomer;
    }

    public void setAssignmentCustomer(AssignmentCustomer assignmentCustomer) {
        this.assignmentCustomer = assignmentCustomer;
    }

    public void setAllocationResponsible(AllocationResponsible allocationResponsible) {
        this.allocationResponsible = allocationResponsible;
    }

    public String getAssignmentTitle() {
        return assignmentTitle;
    }

    public void setAssignmentTitle(String assignmentTitle) {
        this.assignmentTitle = assignmentTitle;
    }

    public AssignmentConsultant getAssignmentConsultant() {
        return assignmentConsultant;
    }

    public void setAssignmentConsultant(AssignmentConsultant assignmentConsultant) {
        this.assignmentConsultant = assignmentConsultant;
    }

    @Override
    public String toString() {
        return "NotificationData{" +
                "isReadyToBeNotified=" + isReadyToBeNotified +
                ", assignmentCustomer=" + assignmentCustomer +
                ", projectId=" + projectId +
                ", assignmentId=" + assignmentId +
                ", assignmentTitle='" + assignmentTitle + '\'' +
                ", assignmentConsultant=" + assignmentConsultant +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", allocationResponsible=" + allocationResponsible +
                '}';
    }
}
