package staffs.leaverequestapp.staff.domain;

import java.time.LocalDate;
import java.util.Objects;

public record Placement(String currentRole, LocalDate roleStartDate, String jobLevel, EmploymentType employmentType) {
    public Placement {
        Objects.requireNonNull(currentRole, "Current role cannot be null");
        Objects.requireNonNull(roleStartDate, "Role start date cannot be null");
        Objects.requireNonNull(jobLevel, "Job level cannot be null");
        Objects.requireNonNull(employmentType, "Employment type cannot be null");
    }
}
