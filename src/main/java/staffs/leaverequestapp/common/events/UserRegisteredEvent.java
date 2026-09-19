package staffs.leaverequestapp.common.events;

import staffs.leaverequestapp.common.domain.FullName;

import java.time.Instant;
import java.util.UUID;

public record UserRegisteredEvent(
        Long id,
        Instant occurredOn,
        String identityId,
        String email,
        String firstName,
        String surname,
        String role
) implements RemoteEvent {

    public UserRegisteredEvent(String identityId, String email, String firstName, String surname, String role) {
        this(null, Instant.now(), identityId, email, firstName, surname, role );
    }

    @Override
    public Long id() {
        return id;
    }

    @Override
    public UserRegisteredEvent withId(Long newId) {
        return new UserRegisteredEvent(
                newId,
                Instant.now(),
                identityId,
                email,
                firstName,
                surname,
                role);
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