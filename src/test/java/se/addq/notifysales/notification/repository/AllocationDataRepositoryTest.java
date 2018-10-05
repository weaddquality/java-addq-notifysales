package se.addq.notifysales.notification.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;
import se.addq.notifysales.notification.model.AllocationResponsible;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
public class AllocationDataRepositoryTest {

    @Mock
    private AllocationResponsibleJpaRepository allocationResponsibleJpaRepository;

    private AllocationResponsibleDataRepository allocationResponsibleDataRepository;

    @Before
    public void setup() {
        allocationResponsibleDataRepository = new AllocationResponsibleDataRepository(allocationResponsibleJpaRepository);
    }

    @Test
    public void saveAllocationResponsibleData() {
        AllocationResponsible allocationResponsible = getAllocationData();
        allocationResponsibleDataRepository.saveAllocationResponsibleData(allocationResponsible);
        verify(allocationResponsibleJpaRepository, times(1)).save(Mockito.any());
    }

    @Test
    public void findAllAllocationResponsibleData() {
        List<AllocationResponsible> allocationResponsibleList = new ArrayList<>();
        allocationResponsibleList.add(getAllocationData());
        Mockito.when(allocationResponsibleJpaRepository.findAll()).thenReturn(allocationResponsibleList);
        List<AllocationResponsible> notificationRepoDataList = allocationResponsibleDataRepository.findAllAllocationResponsible();
        assertThat(notificationRepoDataList.size()).isOne();

    }

    private AllocationResponsible getAllocationData() {
        AllocationResponsible allocationResponsible = new AllocationResponsible();
        allocationResponsible.setSlackUserId("U12345");
        allocationResponsible.setName("Nisse");
        allocationResponsible.setTeamName("Testteam 1");
        allocationResponsible.setTeamId(123);
        return allocationResponsible;
    }
}