package se.addq.notifysales.notification;

public interface NotificationServiceApi {


    void updateAssignmentsToNotify();

    byte[] getAllocationConfiguration();

    String getListOfNotifiedAssignments();
}
