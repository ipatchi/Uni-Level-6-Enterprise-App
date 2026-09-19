package staffs.leaverequestapp.common.events;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class EventStoreService {
    public enum StatusOfMessageDelivery {
        PENDING, PUBLISHED, FAILED, UNROUTABLE
    }

    private final EventStoreRepository eventsStore;
    private final ObjectMapper objectMapper;

    @Transactional
    public EventStoreJpa append(Event event){
        try {
            EventStoreJpa newEventJpa = new EventStoreJpa();
            newEventJpa.setId(null);
            newEventJpa.setEventType(event.getClass().getSimpleName());
            newEventJpa.setOccurredOn(Instant.now());

            // Use this as might not be able to deserialise toString + might change toString at some point
            newEventJpa.setEventBody(objectMapper.writeValueAsString(event));
            newEventJpa.setStatus(StatusOfMessageDelivery.PENDING.name());
            newEventJpa.setRetryCount(0);

            return eventsStore.save(newEventJpa);
        } catch (JacksonException je) {
            throw new IllegalArgumentException("Failed to serialise event payload", je);
        }
    }

    @Transactional
    public void updateStatus(Long eventId, StatusOfMessageDelivery statusOfMessageDelivery, boolean incrementRetryCount){
        eventsStore.findById(eventId).ifPresent(event -> {
            event.setStatus(statusOfMessageDelivery.name());
            if (incrementRetryCount) event.setRetryCount(event.getRetryCount() + 1);
            eventsStore.save(event);
            log.info("Event {} marked as {}", eventId, event.getStatus());
        });
    }
}