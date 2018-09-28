package se.addq.notifysales.notification;

import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.addq.notifysales.cinode.CinodeApi;
import se.addq.notifysales.cinode.model.Team;
import se.addq.notifysales.notification.model.AllocationResponsible;
import se.addq.notifysales.utils.CsvFileHandler;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Component
class AllocationResponsibleHandler {

    private final List<AllocationResponsible> allocationResponsibleList;

    private final CsvFileHandler csvFileHandler;

    private final CinodeApi cinodeApi;

    private static final String ALLOCATION_RESPONSIBLE_SOURCE_FILE_PATH = "allocation_responsible_default.csv";

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    AllocationResponsibleHandler(CsvFileHandler csvFileHandler, CinodeApi cinodeApi) {
        this.csvFileHandler = csvFileHandler;
        this.allocationResponsibleList = getAllocationResponsibleResourceAsList();
        this.cinodeApi = cinodeApi;
    }

    public List<Team> getTeamsForUser(int userId) {
        return cinodeApi.getTeamsForUser(userId);
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
