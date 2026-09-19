package staffs.leaverequestapp.common.events;

import staffs.leaverequestapp.common.domain.FullName;

import java.time.Instant;
import java.util.UUID;

public record StaffMemberHiredEvent(
        Long id,
        Instant occurredOn,
        UUID staffId,
        UUID managerId,
        FullName fullName,
        String identityId
) implements RemoteEvent {

    public StaffMemberHiredEvent(UUID staffId, UUID managerId, FullName fullName, String identityId) {
        this(null, Instant.now(), staffId, managerId, fullName, identityId);
    }

    @Override
    public Long id() {
        return id;
    }

    @Override
    public StaffMemberHiredEvent withId(Long newId) {
        return new StaffMemberHiredEvent(newId, this.occurredOn, this.staffId, this.managerId, this.fullName, this.identityId);
    }

    @Override
    public String exchange() {
        return "";
    }

    @Override
    public String routingKey() {
        return "";
    }
}