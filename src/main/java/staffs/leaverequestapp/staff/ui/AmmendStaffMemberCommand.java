package staffs.leaverequestapp.staff.ui;

import staffs.leaverequestapp.staff.domain.EmploymentStatus;
import staffs.leaverequestapp.staff.domain.EmploymentType;

import java.util.UUID;

public record AmmendStaffMemberCommand(
        UUID staffId,
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
    public AmmendStaffMemberCommand withStaffId(UUID id) {
        return new AmmendStaffMemberCommand(
                id,
                this.newFirstName,
                this.newSurname,
                this.newEmail,
                this.newDepartment,
                this.newManagerId,
                this.newRole,
                this.newJobLevel,
                this.newEmploymentType,
                this.newStatus
        );
    }
}
