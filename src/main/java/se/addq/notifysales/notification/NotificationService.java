package se.addq.notifysales.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.addq.notifysales.cinode.model.AssignmentResponse;
import se.addq.notifysales.notification.model.NotificationData;
import se.addq.notifysales.utils.JsonUtil;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;


@Service
class NotificationService implements NotificationServiceApi {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final AssignmentHandler assignmentHandler;

    private final AllocationResponsibleHandler allocationResponsibleHandler;

    private final NotificationHandler notificationHandler;

    @Autowired
    NotificationService(NotificationHandler notificationHandler, AllocationResponsibleHandler allocationResponsibleHandler, AssignmentHandler assignmentHandler) {
        this.notificationHandler = notificationHandler;
        this.allocationResponsibleHandler = allocationResponsibleHandler;
        this.assignmentHandler = assignmentHandler;
    }

    @Override
    public void updateAssignmentsToNotify() {
        List<AssignmentResponse> endingAssignments = assignmentHandler.getEndingAssignments();
        List<AssignmentResponse> filteredEndingAssignments = filterOutAssignmentsNotApplicableForNotification(endingAssignments);
        List<NotificationData> notificationDataList = notificationHandler.addAssignmentsToNotificationList(filteredEndingAssignments);
        List<NotificationData> notificationDataListWithAllocationResp = allocationResponsibleHandler.setAllocationResponsible(notificationDataList);
        notificationHandler.assignmentsToNotifyAdd(notificationDataListWithAllocationResp);
    }


    @Override
    public String getListOfNotifiedAssignments() {
        return JsonUtil.getJsonFromObject(notificationHandler.getAlreadyNotifiedAssignments(), true);
    }


    private List<AssignmentResponse> filterOutAssignmentsNotApplicableForNotification(List<AssignmentResponse> endingAssignments) {
        List<AssignmentResponse> assignmentResponsesToRemove = new ArrayList<>();
        for (AssignmentResponse assignmentResponse : endingAssignments) {
            if (!notificationHandler.isToBeNotified(assignmentResponse.getId())) {
                assignmentResponsesToRemove.add(assignmentResponse);
            }
        }
        endingAssignments.removeAll(assignmentResponsesToRemove);
        return endingAssignments;
    }


}
