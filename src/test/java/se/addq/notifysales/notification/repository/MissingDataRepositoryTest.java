package se.addq.notifysales.notification.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;
import se.addq.notifysales.notification.model.MissingDataType;
import se.addq.notifysales.notification.model.MissingNotificationData;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
public class MissingDataRepositoryTest {

    private MissingDataRepository missingDataRepository;

    @Mock
    private MissingNotificationDataJpaRepository missingNotificationDataJpaRepositoryMock;

    @Before
    public void setup() {
        missingDataRepository = new MissingDataRepository(missingNotificationDataJpaRepositoryMock);
        Mockito.when(missingNotificationDataJpaRepositoryMock.findAll()).thenReturn(getMissingNotificationData());
    }

    @Test
    public void saveMissingNotificationDataWillReturnListOfData() {
        missingDataRepository.saveMissingNotificationData(getMissingNotificationData().iterator().next());
        verify(missingNotificationDataJpaRepositoryMock, times(1)).save(Mockito.any());
    }

    @Test
    public void findAllNotificationDataWillReturnListOfAllData() {
        List<MissingNotificationData> missingNotificationDataList = missingDataRepository.findAllNotificationData();
        assertThat(missingNotificationDataList.size()).isEqualTo(2);
    }


    @Test
    public void delete() {
        missingDataRepository.delete(getMissingNotificationData().iterator().next());
        verify(missingNotificationDataJpaRepositoryMock, times(1)).delete(Mockito.any());
    }

    @Test
    public void findByAssignmentId() {
        missingDataRepository.findByAssignmentId(123);
        verify(missingNotificationDataJpaRepositoryMock, times(1)).findByAssignmentId(Mockito.anyInt());
    }


    private Iterable<MissingNotificationData> getMissingNotificationData() {
        ArrayList<MissingNotificationData> list = new ArrayList<>();
        MissingNotificationData missingNotificationData = new MissingNotificationData();
        missingNotificationData.setAssignmentId(123);
        missingNotificationData.setMissingdataType(MissingDataType.MISSING_ALLOCATION_RESPONSIBLE);
        missingNotificationData.setMissingData("Data");
        list.add(missingNotificationData);
        missingNotificationData = new MissingNotificationData();
        missingNotificationData.setAssignmentId(124);
        missingNotificationData.setMissingdataType(MissingDataType.MISSING_TEAM_FOR_USER);
        missingNotificationData.setMissingData("Data");
        list.add(missingNotificationData);
        return list;
    }

}