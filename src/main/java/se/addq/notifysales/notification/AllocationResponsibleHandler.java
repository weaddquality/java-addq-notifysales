package se.addq.notifysales.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.addq.notifysales.cinode.CinodeApi;
import se.addq.notifysales.cinode.model.Team;
import se.addq.notifysales.notification.model.AllocationResponsible;
import se.addq.notifysales.notification.model.MissingDataType;
import se.addq.notifysales.notification.model.NotificationData;
import se.addq.notifysales.notification.repository.AllocationResponsibleDataRepository;
import se.addq.notifysales.utils.SleepUtil;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Component
class AllocationResponsibleHandler {

    private final List<AllocationResponsible> allocationResponsibleList;

    private final CinodeApi cinodeApi;

    private MissingDataHandler missingDataHandler;

    private AllocationResponsibleDataRepository allocationResponsibleDataRepository;

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final List<NotificationData> incompleteNotificationDataToBeRemoved = new ArrayList<>();

    @Autowired
    AllocationResponsibleHandler(MissingDataHandler missingDataHandler, CinodeApi cinodeApi, AllocationResponsibleDataRepository allocationResponsibleDataRepository) {
        this.allocationResponsibleDataRepository = allocationResponsibleDataRepository;
        this.allocationResponsibleList = getAllocationResponsibleResourceAsList();
        this.cinodeApi = cinodeApi;
        this.missingDataHandler = missingDataHandler;

    }


    List<NotificationData> setAllocationResponsible(List<NotificationData> notificationDataList) {
        incompleteNotificationDataToBeRemoved.clear();
        for (NotificationData notificationData : notificationDataList) {
            List<Team> teams = cinodeApi.getTeamsForUser(notificationData.getAssignmentConsultant().getUserId());
            if (teams.isEmpty()) {
                log.warn("Missing team for user {} in Cinode for {} will remove from list to notify", notificationData.getAssignmentConsultant().getFirstName() + notificationData.getAssignmentConsultant().getLastName(), notificationData);
                missingDataHandler.addMissingData(notificationData, MissingDataType.MISSING_TEAM_FOR_USER, notificationData.getAssignmentConsultant().toString());
                incompleteNotificationDataToBeRemoved.add(notificationData);
                continue;
            }
            notificationData.getAssignmentConsultant().setTeamName(teams.get(0).getName());
            notificationData.getAssignmentConsultant().setTeamId(teams.get(0).getId());

            SleepUtil.sleepMilliSeconds(500);
            AllocationResponsible allocationResponsible = getAllocationResponsibleForTeam(teams.get(0));
            if (allocationResponsible.getName() == null || allocationResponsible.getName().equals("")) {
                log.warn("Missing configuration for team {}, will remove from notification list", teams.get(0).getName());
                missingDataHandler.addMissingData(notificationData, MissingDataType.MISSING_ALLOCATION_RESPONSIBLE, teams.get(0).getName());
                incompleteNotificationDataToBeRemoved.add(notificationData);
                continue;
            }
            notificationData.setAllocationResponsible(allocationResponsible);
            notificationData.setReadyToBeNotified(true);
            missingDataHandler.removeFromMissingDataIfExisting(notificationData.getAssignmentId());
        }
        notificationDataList.removeAll(incompleteNotificationDataToBeRemoved);
        return notificationDataList;
    }


    AllocationResponsible getAllocationResponsibleForTeam(Team team) {
        for (AllocationResponsible allocationResponsible : allocationResponsibleList) {
            if (allocationResponsible.getTeamId() == team.getId()) {
                log.info("Got responsible for allocation of team {} , {}", allocationResponsible.getTeamName(), allocationResponsible.getName());
                return allocationResponsible;
            }
        }
        log.warn("No configuration of allocation responsible for team {} ", team);
        return new AllocationResponsible();
    }

    private List<AllocationResponsible> getAllocationResponsibleResourceAsList() {
        return allocationResponsibleDataRepository.findAllAllocationResponsible();
    }


}
