package se.addq.notifysales.notification.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import se.addq.notifysales.notification.model.AllocationResponsible;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class AllocationResponsibleJpaRepositoryTest {

    @Autowired
    private AllocationResponsibleJpaRepository allocationResponsibleJpaRepository;

    @Before
    public void setUp() {
        AllocationResponsible allocationResponsible = getAllocationResponsible();
        assertThat(allocationResponsible.getId()).isNull();
        this.allocationResponsibleJpaRepository.save(allocationResponsible);
        assertThat(allocationResponsible.getId()).isNotNull();
    }

    private AllocationResponsible getAllocationResponsible() {
        AllocationResponsible allocationResponsible = new AllocationResponsible();
        allocationResponsible.setTeamId(123);
        allocationResponsible.setTeamName("Test team 1");
        allocationResponsible.setName("Nisse säljare");
        allocationResponsible.setSlackUserId("U12345");
        return allocationResponsible;
    }

    @Test
    public void findAllWillReturnAllNotificationRepoDataInNotificationDb() {
        Iterable<AllocationResponsible> allocationResponsibleIterable = allocationResponsibleJpaRepository.findAll();
        assertThat(allocationResponsibleIterable.spliterator().getExactSizeIfKnown()).isEqualTo(1);
        assertThat(allocationResponsibleIterable.iterator().next().getName()).isEqualTo(getAllocationResponsible().getName());
    }

    @After
    public void tearDown() {
        this.allocationResponsibleJpaRepository.deleteAll();
    }
}