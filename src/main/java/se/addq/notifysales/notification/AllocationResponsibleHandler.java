package se.addq.notifysales.notification;

import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.addq.notifysales.cinode.CinodeApi;
import se.addq.notifysales.cinode.model.Team;
import se.addq.notifysales.notification.model.AllocationResponsible;
import se.addq.notifysales.notification.model.NotificationData;
import se.addq.notifysales.utils.CsvFileHandler;
import se.addq.notifysales.utils.SleepUtil;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Component
class AllocationResponsibleHandler {

    private final List<AllocationResponsible> allocationResponsibleList;

    private final CsvFileHandler csvFileHandler;

    private final CinodeApi cinodeApi;

    private MissingDataHandler missingDataHandler;

    private static final String ALLOCATION_RESPONSIBLE_SOURCE_FILE_PATH = "allocation_responsible_default.csv";

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final List<NotificationData> incompleteNotificationDataToBeRemoved = new ArrayList<>();

    @Autowired
    AllocationResponsibleHandler(CsvFileHandler csvFileHandler, MissingDataHandler missingDataHandler, CinodeApi cinodeApi) {
        this.csvFileHandler = csvFileHandler;
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
                missingDataHandler.addTeamIsMissingForUser(notificationData);
                incompleteNotificationDataToBeRemoved.add(notificationData);
                continue;
            }
            notificationData.getAssignmentConsultant().setTeamName(teams.get(0).getName());
            notificationData.getAssignmentConsultant().setTeamId(teams.get(0).getId());

            SleepUtil.sleepMilliSeconds(500);
            AllocationResponsible allocationResponsible = getAllocationResponsibleForTeam(teams.get(0));
            if (allocationResponsible.getName() == null || allocationResponsible.getName().equals("")) {
                log.warn("Missing configuration for team {}, will remove from notification list", teams.get(0).getName());
                missingDataHandler.addAllocationResponsibleIsMissingForTeam(notificationData, teams.get(0).getName());
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


    byte[] getAllocationResponsibleListAsByteArray() {
        return csvFileHandler.getListOfCSVRecordsAsByteArray(mapAllocationResponsibleToListOfStringArray(allocationResponsibleList), AllocationCsvHeaders.class);
    }


    private List<AllocationResponsible> getAllocationResponsibleResourceAsList() {
        List<CSVRecord> csvRecordList = csvFileHandler.getListOfCSVRecords(ALLOCATION_RESPONSIBLE_SOURCE_FILE_PATH, AllocationCsvHeaders.class);
        return mapCsvToAllocationResponsible(csvRecordList);
    }


    private List<AllocationResponsible> mapCsvToAllocationResponsible(List<CSVRecord> csvRecordList) {
        List<AllocationResponsible> allocationResponsibleList = new ArrayList<>();
        for (CSVRecord csvRecord : csvRecordList) {
            AllocationResponsible allocationResponsible = new AllocationResponsible();
            allocationResponsible.setName(csvRecord.get(AllocationCsvHeaders.NAME));
            allocationResponsible.setTeamName(csvRecord.get(AllocationCsvHeaders.TEAM_NAME));
            allocationResponsible.setTeamId(Integer.parseInt(csvRecord.get(AllocationCsvHeaders.TEAM_ID)));
            allocationResponsible.setSlackUserId(csvRecord.get(AllocationCsvHeaders.SLACK_USER_ID));
            allocationResponsible.setSlackChannel(csvRecord.get(AllocationCsvHeaders.SLACK_CHANNEL));
            allocationResponsibleList.add(allocationResponsible);
        }
        return allocationResponsibleList;
    }

    private List<String[]> mapAllocationResponsibleToListOfStringArray(List<AllocationResponsible> allocationResponsibles) {
        List<String[]> allocationResponsibleList = new ArrayList<>();
        for (AllocationResponsible allocationResponsible : allocationResponsibles) {
            String[] csvRecordData = {allocationResponsible.getName(), allocationResponsible.getTeamName(),
                    String.valueOf(allocationResponsible.getTeamId()), allocationResponsible.getSlackUserId(),
                    allocationResponsible.getSlackChannel()};
            allocationResponsibleList.add(csvRecordData);
        }
        return allocationResponsibleList;
    }


}
