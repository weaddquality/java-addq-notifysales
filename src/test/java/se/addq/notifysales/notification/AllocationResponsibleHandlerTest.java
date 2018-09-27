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
import se.addq.notifysales.cinode.model.Team;
import se.addq.notifysales.notification.model.AllocationResponsible;
import se.addq.notifysales.utils.CsvFileHandler;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class AllocationResponsibleHandlerTest {

    @Mock
    private CsvFileHandler mockCsvFileHandler;

    @Autowired
    private ResourceLoader resourceLoader;

    private AllocationResponsibleHandler allocationResponsibleHandler;

    @Before
    public void setup() {
        CsvFileHandler csvFileHandler = new CsvFileHandler(resourceLoader);
        List<CSVRecord> csvRecordList = csvFileHandler.getListOfCSVRecords("allocation_responsible_test.csv", AllocationCsvHeaders.class);
        Mockito.when(mockCsvFileHandler.getListOfCSVRecords(Mockito.any(), Mockito.any())).thenReturn(csvRecordList);
        Mockito.when(mockCsvFileHandler.getListOfCSVRecordsAsByteArray(Mockito.anyList(), Mockito.any())).thenReturn(new byte[256]);
        allocationResponsibleHandler = new AllocationResponsibleHandler(mockCsvFileHandler);
    }

    @Test
    public void getAllocationResponsibleForExistingTeamId() {
        Team team = new Team();
        team.setName("ADDQ 2 Front-End auto");
        team.setId(1406);
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


}
