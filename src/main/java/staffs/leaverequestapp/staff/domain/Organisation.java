package staffs.leaverequestapp.staff.domain;

import java.time.LocalDate;
import java.util.UUID;

public record Organisation(LocalDate hireDate, String department, UUID lineManagerId) {
    public Organisation {
        if (hireDate == null) throw new IllegalArgumentException("Hire date cannot be null");
        if (department == null || department.isBlank()) throw new IllegalArgumentException("Department cannot be blank");
    }
}
