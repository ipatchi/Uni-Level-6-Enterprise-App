package staffs.leaverequestapp.staff.domain.events;

import staffs.leaverequestapp.common.events.RemoteEvent;
import staffs.leaverequestapp.common.events.Event;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record StaffMemberHiredEvent(
        Long id,
        Instant occurredOn,
        UUID staffId
) implements RemoteEvent {

    public StaffMemberHiredEvent(UUID staffId) {
        this(null, Instant.now(), staffId);
    }

    @Override
    public String exchange() {
        return "staff.exchange";
    }

    @Override
    public String routingKey() {
        return "staff.hired";
    }

    @Override
    public Event withId(Long newId) {
        return new StaffMemberHiredEvent(newId, this.occurredOn, this.staffId);
    }
}