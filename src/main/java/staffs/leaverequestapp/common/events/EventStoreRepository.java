package staffs.leaverequestapp.common.events;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventStoreRepository extends CrudRepository<EventStoreJpa, Long> {
}

