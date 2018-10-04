package se.addq.notifysales.notification;

import org.apache.commons.csv.CSVRecord;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit4.SpringRunner;
import se.addq.notifysales.cinode.CinodeApi;
import se.addq.notifysales.cinode.model.Team;
import se.addq.notifysales.notification.model.AllocationResponsible;
import se.addq.notifysales.notification.model.AssignmentConsultant;
import se.addq.notifysales.notification.model.NotificationData;
import se.addq.notifysales.utils.CsvFileHandler;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
public class AllocationResponsibleHandlerTest {

    private static final int TEAM_ID_EXISTING_IN_CSV_TEST_FILE = 1206;

    @Mock
    private CsvFileHandler mockCsvFileHandler;

    @Mock
    private CinodeApi cinodeApi;

    @Mock
    private MissingDataHandler missingDataHandler;

    @Autowired
    private ResourceLoader resourceLoader;

    private AllocationResponsibleHandler allocationResponsibleHandler;

    @Before
    public void setup() {
        CsvFileHandler csvFileHandler = new CsvFileHandler(resourceLoader);
        List<CSVRecord> csvRecordList = csvFileHandler.getListOfCSVRecords("allocation_responsible_test.csv", AllocationCsvHeaders.class);
        Mockito.when(mockCsvFileHandler.getListOfCSVRecords(Mockito.any(), Mockito.any())).thenReturn(csvRecordList);
        Mockito.when(mockCsvFileHandler.getListOfCSVRecordsAsByteArray(Mockito.anyList(), Mockito.any())).thenReturn(new byte[256]);
        allocationResponsibleHandler = new AllocationResponsibleHandler(mockCsvFileHandler, missingDataHandler, cinodeApi);
    }

    @Test
    public void getAllocationResponsibleForExistingTeamId() {
        Team team = new Team();
        team.setName("Test team 1");
        team.setId(1206);
        team.setDescription("The good team!");
        AllocationResponsible allocationResponsible = allocationResponsibleHandler.getAllocationResponsibleForTeam(team);
        assertThat(allocationResponsible.getName()).isEqualTo("Nisse");
    }

    @Test
    public void getAllocationResponsibleForNotExistingTeamIdEmptyDataReturned() {
        Team team = new Team();
        team.setName("ADDQ 2 Front-End auto");
        team.setId(1);
        team.setDescription("The good team!");
        AllocationResponsible allocationResponsible = allocationResponsibleHandler.getAllocationResponsibleForTeam(team);
        assertThat(allocationResponsible.getName()).isEqualTo("");
    }

    @Test
    public void getByteArrayForAllocationResponsibleList() {
        byte[] responsibleListAsByteArray = allocationResponsibleHandler.getAllocationResponsibleListAsByteArray();
        assertThat(responsibleListAsByteArray.length).isEqualTo(256);
    }

    @Test
    public void setAllocationResponsibleNoTeamForUserDataRemoved() {
        List<NotificationData> notificationDataList = allocationResponsibleHandler.setAllocationResponsible(getNotificationDataList());
        assertThat(notificationDataList.size()).isZero();
        verify(missingDataHandler, times(1)).addTeamIsMissingForUser(Mockito.any());
    }

    @Test
    public void setAllocationResponsibleTeamExistsForUserNoAllocationResponsibleForTeamDataRemoved() {
        List<Team> list = getListOfTeamsForUser();
        list.get(0).setId(TEAM_ID_EXISTING_IN_CSV_TEST_FILE + 1);
        Mockito.when(cinodeApi.getTeamsForUser(Mockito.anyInt())).thenReturn(list);
        List<NotificationData> notificationDataList = allocationResponsibleHandler.setAllocationResponsible(getNotificationDataList());
        assertThat(notificationDataList.size()).isZero();
        verify(missingDataHandler, times(1)).addAllocationResponsibleIsMissingForTeam(Mockito.any(), Mockito.anyString());
    }

    @Test
    public void setAllocationResponsibleTeamForUserDataAdded() {
        Mockito.when(cinodeApi.getTeamsForUser(Mockito.anyInt())).thenReturn(getListOfTeamsForUser());
        List<NotificationData> notificationDataList = allocationResponsibleHandler.setAllocationResponsible(getNotificationDataList());
        assertThat(notificationDataList.size()).isOne();
        verify(missingDataHandler, times(1)).removeFromMissingDataIfExisting(Mockito.anyInt());
    }

    private List<Team> getListOfTeamsForUser() {
        List<Team> teamList = new ArrayList<>();
        Team team = new Team();
        team.setName("Test team 1");
        team.setId(TEAM_ID_EXISTING_IN_CSV_TEST_FILE);
        teamList.add(team);
        return teamList;
    }


    private List<NotificationData> getNotificationDataList() {
        List<NotificationData> notificationDataList = new ArrayList<>();
        NotificationData notificationData = new NotificationData();
        notificationData.setAssignmentId(123);
        AssignmentConsultant assignmentConsultant = new AssignmentConsultant();
        assignmentConsultant.setFirstName("Kalle");
        assignmentConsultant.setLastName("Kula");
        notificationData.setAssignmentConsultant(assignmentConsultant);
        notificationDataList.add(notificationData);
        return notificationDataList;
    }


}
