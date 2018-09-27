package se.addq.notifysales.utils;


import org.apache.commons.csv.CSVRecord;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit4.SpringRunner;
import se.addq.notifysales.notification.AllocationCsvHeaders;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
public class CsvFileHandlerTest {

    @Autowired
    private ResourceLoader resourceLoader;

    private CsvFileHandler csvFileHandler;

    private static final String ALLOCATION_RESPONSIBLE_SOURCE_TEST_FILE_PATH = "allocation_responsible_test.csv";

    @Before
    public void setup() {
        csvFileHandler = new CsvFileHandler(resourceLoader);
    }

    @Test
    public void getListOfCsvRecordsReturnsListOfRecordsSizeNotZero() {
        List<CSVRecord> listOfCSVRecords = csvFileHandler.getListOfCSVRecords(ALLOCATION_RESPONSIBLE_SOURCE_TEST_FILE_PATH, AllocationCsvHeaders.class);
        assertThat(listOfCSVRecords.size()).isNotZero();
    }

    @Test
    public void getListOfCsvRecordsMissingFileEmptyListReturned() {
        List<CSVRecord> listOfCSVRecords = csvFileHandler.getListOfCSVRecords("not_existing.csv", AllocationCsvHeaders.class);
        assertThat(listOfCSVRecords.size()).isZero();
    }

    @Test
    public void getListOfStringArrayAsByteArrayAndCheckLength() {
        List<String[]> allocationResponsibleList = new ArrayList<>();
        String[] csvRecordData = {"Nisse", "Super Team", "1", "U12345", "The test channel"};

        allocationResponsibleList.add(csvRecordData);
        byte[] recordsAsByteArray = csvFileHandler.getListOfCSVRecordsAsByteArray(allocationResponsibleList, AllocationCsvHeaders.class);
        assertThat(recordsAsByteArray.length).isEqualTo(96);
    }

    @Test
    public void getListOfStringArrayAsByteArrayListIsNullEmptyListReturned() {
        byte[] recordsAsByteArray = csvFileHandler.getListOfCSVRecordsAsByteArray(null, AllocationCsvHeaders.class);
        assertThat(recordsAsByteArray.length).isEqualTo(0);
    }

    @Test
    public void getListOfCsvRecordsReturnsListWithMappingOfColumnsName() {
        List<CSVRecord> listOfCSVRecords = csvFileHandler.getListOfCSVRecords(ALLOCATION_RESPONSIBLE_SOURCE_TEST_FILE_PATH, AllocationCsvHeaders.class);
        assertThat(listOfCSVRecords.get(0).isMapped("NAME")).isTrue();
    }

    @Test
    public void getListOfCsvRecordsReturnsListWithMappingOfColumnsTeamName() {
        List<CSVRecord> listOfCSVRecords = csvFileHandler.getListOfCSVRecords(ALLOCATION_RESPONSIBLE_SOURCE_TEST_FILE_PATH, AllocationCsvHeaders.class);
        assertThat(listOfCSVRecords.get(0).isMapped("TEAM_NAME")).isTrue();
    }

    @Test
    public void getListOfCsvRecordsReturnsListWithMappingOfColumnsTeamId() {
        List<CSVRecord> listOfCSVRecords = csvFileHandler.getListOfCSVRecords(ALLOCATION_RESPONSIBLE_SOURCE_TEST_FILE_PATH, AllocationCsvHeaders.class);
        assertThat(listOfCSVRecords.get(0).isMapped("TEAM_ID")).isTrue();
    }

    @Test
    public void getListOfCsvRecordsReturnsListWithMappingOfColumnsSlackUserId() {
        List<CSVRecord> listOfCSVRecords = csvFileHandler.getListOfCSVRecords(ALLOCATION_RESPONSIBLE_SOURCE_TEST_FILE_PATH, AllocationCsvHeaders.class);
        assertThat(listOfCSVRecords.get(0).isMapped("SLACK_USER_ID")).isTrue();
    }

    @Test
    public void getListOfCsvRecordsReturnsListWithMappingOfColumnsSlackChannel() {
        List<CSVRecord> listOfCSVRecords = csvFileHandler.getListOfCSVRecords(ALLOCATION_RESPONSIBLE_SOURCE_TEST_FILE_PATH, AllocationCsvHeaders.class);
        assertThat(listOfCSVRecords.get(0).isMapped("SLACK_CHANNEL")).isTrue();
    }


}
