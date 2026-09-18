package staffs.leaverequestapp.staff.persistance.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import staffs.leaverequestapp.staff.domain.EmploymentType;

import java.time.LocalDate;

@Embeddable
@Getter
@Setter
public class PlacementJpa {
    @Column(nullable = false, name = "job_role")
    private String CurrentRole;

    @Column(nullable = false)
    private LocalDate roleStartDate;

    @Column(nullable = false)
    private String jobLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmploymentType employmentType;
}
