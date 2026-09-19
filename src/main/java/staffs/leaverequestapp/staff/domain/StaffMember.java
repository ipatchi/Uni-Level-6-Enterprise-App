package staffs.leaverequestapp.staff.domain;

import lombok.Getter;
import staffs.leaverequestapp.common.domain.AggregateRoot;
import staffs.leaverequestapp.common.domain.FullName;
import staffs.leaverequestapp.common.domain.Identity;
import staffs.leaverequestapp.common.events.StaffMemberAmendedEvent;
import staffs.leaverequestapp.common.events.StaffMemberHiredEvent;
import staffs.leaverequestapp.staff.persistance.entities.StaffJpa;

import java.time.LocalDate;
import java.util.UUID;

@Getter
public class StaffMember extends AggregateRoot<StaffMember> {

    private FullName identity;
    private String email;
    private Organisation organisation;
    private Placement placement;
    private UUID managerId;
    private EmploymentStatus employmentStatus;
    private String identityId;

    private StaffMember(
            Identity<StaffMember> id,
            FullName identity,
            String email,
            Organisation organisation,
            Placement placement,
            UUID managerId,
            EmploymentStatus employmentStatus,
            String identityId
    ) {
        super(id);
        this.identity = identity;
        this.email = email;
        this.organisation = organisation;
        this.placement = placement;
        this.managerId = managerId;
        this.employmentStatus = employmentStatus;
        this.identityId = identityId;
    }

    public static StaffMember hire(
            Identity<StaffMember> id,
            FullName identity,
            String email,
            Organisation organisation,
            Placement placement,
            UUID managerId,
            String identityId
    ) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("A valid email address is required");
        }

        StaffMember staff = new StaffMember(id, identity, email, organisation, placement, managerId, EmploymentStatus.ACTIVE, identityId);

        staff.addDomainEvent(new StaffMemberHiredEvent(
                UUID.fromString(id.id()),
                managerId,
                identity,
                identityId
        ));

        return staff;
    }

    public static StaffMember restore(
            Identity<StaffMember> id,
            FullName identity,
            String email,
            Organisation organisation,
            Placement placement,
            UUID managerId,
            EmploymentStatus employmentStatus,
            String identityId
    ) {
        return new StaffMember(id, identity, email, organisation, placement, managerId, employmentStatus, identityId);
    }

    public void updateDetails(
            String newFirstName,
            String newSurname,
            String newEmail,
            String newDepartment,
            UUID newManagerId,
            String newRole,
            String newJobLevel,
            EmploymentType newEmploymentType,
            EmploymentStatus newStatus
    ) {
        if (newFirstName != null || newSurname != null) {
            String first = newFirstName != null ? newFirstName : this.identity.firstName();
            String last = newSurname != null ? newSurname : this.identity.surname();
            this.identity = new FullName(first, last);
        }

        if (newEmail != null) {
            this.email = newEmail;
        }
        if (newManagerId != null) {
            this.managerId = newManagerId;
        }
        if (newStatus != null) {
            this.employmentStatus = newStatus;
        }

        if (newDepartment != null) {
            String dept = newDepartment != null ? newDepartment : this.organisation.department();
            this.organisation = new Organisation(this.organisation.hireDate(), dept, newManagerId);
        }

        if (newRole != null || newJobLevel != null || newEmploymentType != null) {
            String role = newRole != null ? newRole : this.placement.currentRole();
            String level = newJobLevel != null ? newJobLevel : this.placement.jobLevel();
            EmploymentType type = newEmploymentType != null ? newEmploymentType : this.placement.employmentType();

            this.placement = new Placement(role, LocalDate.now(), level, type);
        }

        this.addDomainEvent(new StaffMemberAmendedEvent(
                this.getStaffId(),
                this.managerId,
                this.identity
        ));
    }

    public UUID getStaffId() {
        return UUID.fromString(id.id());
    }
}