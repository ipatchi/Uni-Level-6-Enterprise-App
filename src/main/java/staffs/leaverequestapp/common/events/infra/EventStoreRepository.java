package staffs.leaverequestapp.common.events.infra;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventStoreRepository extends CrudRepository<EventStoreJpa, Long> {
}

