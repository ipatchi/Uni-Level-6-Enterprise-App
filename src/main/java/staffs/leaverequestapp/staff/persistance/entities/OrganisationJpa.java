package staffs.leaverequestapp.staff.persistance.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Embeddable
@Getter
@Setter
public class OrganisationJpa {
    @Column(nullable = false)
    private LocalDate hireDate;

    @Column(nullable = false)
    private String department;

    @Column(nullable = true)
    private UUID lineManagerId;
}
