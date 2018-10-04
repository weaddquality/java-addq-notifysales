package se.addq.notifysales.notification;

public interface NotificationServiceApi {


    void updateAssignmentsToNotify();

    byte[] getAllocationResponsibleConfiguration();

    String getListOfNotifiedAssignments();
}
