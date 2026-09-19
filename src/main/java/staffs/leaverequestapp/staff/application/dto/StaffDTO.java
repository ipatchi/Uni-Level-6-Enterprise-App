package staffs.leaverequestapp.staff.application.dto;

import staffs.leaverequestapp.staff.domain.EmploymentStatus;
import staffs.leaverequestapp.staff.domain.EmploymentType;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public record StaffDTO(
        UUID id,
        String firstName,
        String surname,
        String email,
        OrganisationDTO organisation,
        PlacementDTO placement,
        EmploymentStatus status,
        String identityId
) {
    public StaffDTO {
        Objects.requireNonNull(id, "ID cannot be null");
        Objects.requireNonNull(firstName, "First name cannot be null");
        Objects.requireNonNull(surname, "Surname cannot be null");
        Objects.requireNonNull(email, "Email cannot be null");
        Objects.requireNonNull(organisation, "Organisation details cannot be null");
        Objects.requireNonNull(placement, "Placement details cannot be null");
        Objects.requireNonNull(status, "Employment status cannot be null");
    }

    public record OrganisationDTO(LocalDate hireDate, String department, UUID lineManagerId) {}
    public record PlacementDTO(String currentRole, LocalDate roleStartDate, String jobLevel, EmploymentType employmentType) {}
}
