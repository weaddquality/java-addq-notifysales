package se.addq.notifysales.notification.repository;

import org.springframework.data.repository.CrudRepository;
import se.addq.notifysales.notification.model.AllocationResponsible;

public interface AllocationResponsibleJpaRepository extends CrudRepository<AllocationResponsible, Long> {
    AllocationResponsible findById(long id);
}
