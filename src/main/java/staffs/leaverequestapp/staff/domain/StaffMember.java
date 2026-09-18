package staffs.leaverequestapp.staff.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import staffs.leaverequestapp.common.domain.AggregateRoot;
import staffs.leaverequestapp.common.domain.FullName;
import staffs.leaverequestapp.common.domain.Identity;
import staffs.leaverequestapp.staff.domain.events.StaffMemberHiredEvent;
import staffs.leaverequestapp.staff.persistance.entities.StaffJpa;

import java.util.UUID;

@Getter
public class StaffMember extends AggregateRoot<StaffJpa> {

    private final FullName identity;
    private final String email;
    private final Organisation organisation;
    private final Placement placement;
    private EmploymentStatus employmentStatus;

    private StaffMember(
            Identity<StaffJpa> id,
            FullName identity,
            String email,
            Organisation organisation,
            Placement placement,
            EmploymentStatus employmentStatus
    ) {
        super(id);
        this.identity = identity;
        this.email = email;
        this.organisation = organisation;
        this.placement = placement;
        this.employmentStatus = employmentStatus;
    }

    public static StaffMember hire(
            Identity<StaffJpa> id,
            FullName identity,
            String email,
            Organisation organisation,
            Placement placement
    ) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("A valid email address is required");
        }

        StaffMember staff = new StaffMember(id, identity, email, organisation, placement, EmploymentStatus.ACTIVE);

        staff.addDomainEvent(new StaffMemberHiredEvent(UUID.fromString(id.id())));

        return staff;
    }

    public UUID getStaffId() {
        return UUID.fromString(id.id());
    }
}