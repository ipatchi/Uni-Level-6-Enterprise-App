package staffs.leaverequestapp.common.events;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@AllArgsConstructor
public class DomainEventManager {
    private final ApplicationEventPublisher eventPublisher;
    private final EventStoreService eventStoreService;

    @Transactional // Storing events matches the caller's transactional state
    public void manageDomainEvents(String sourceContext, List<Event> events) {
        Objects.requireNonNull(sourceContext, "Context cannot be null");
        Objects.requireNonNull(events, "Events cannot be null");

        for (Event event : events){
            log.info("{} -> {}", sourceContext, event);

            // Save to our local events DB and retrieve the DB event id (allows for event status changes via id)
            EventStoreJpa savedEvent = eventStoreService.append(event);

            eventPublisher.publishEvent(event.withId(savedEvent.getId()));
        }
    }
}

