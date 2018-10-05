package se.addq.notifysales.notification.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import se.addq.notifysales.notification.model.AllocationResponsible;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

public class AllocationResponsibleDataRepository {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final AllocationResponsibleJpaRepository allocationResponsibleJpaRepository;

    @Autowired
    public AllocationResponsibleDataRepository(AllocationResponsibleJpaRepository allocationResponsibleJpaRepository) {
        this.allocationResponsibleJpaRepository = allocationResponsibleJpaRepository;
    }

    public List<AllocationResponsible> findAllAllocationResponsible() {
        log.info("Get saved notification data from storage");
        List<AllocationResponsible> allocationResponsibleList = new ArrayList<>();
        Iterable<AllocationResponsible> allocationResponsibleIterable = allocationResponsibleJpaRepository.findAll();
        allocationResponsibleIterable.forEach(allocationResponsibleList::add);
        log.info("Got {} allocation responsible items from DB", allocationResponsibleList.size());
        return allocationResponsibleList;
    }

    void saveAllocationResponsibleData(AllocationResponsible allocationResponsible) {
        allocationResponsibleJpaRepository.save(allocationResponsible);
    }
}
