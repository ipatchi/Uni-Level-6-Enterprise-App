package staffs.leaverequestapp.staff.ui;

import staffs.leaverequestapp.staff.domain.EmploymentStatus;
import staffs.leaverequestapp.staff.domain.EmploymentType;
import java.time.LocalDate;
import java.util.UUID;

public record CreateStaffMemberCommand(
        String firstName,
        String surname,
        String email,
        LocalDate hireDate,
        String department,
        UUID managerId,
        String currentRole,
        LocalDate roleStartDate,
        String jobLevel,
        EmploymentType employmentType,
        EmploymentStatus status,
        String identityId
) {}
