package staffs.leaverequestapp.staff.persistance.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import staffs.leaverequestapp.common.domain.FullName;
import staffs.leaverequestapp.staff.domain.EmploymentStatus;

import java.util.UUID;

@Entity(name = "staff")
@Table(name ="staff_members")
@Getter
@Setter
@ToString
public class StaffJpa {
    @Id
    @Column(name="staffId")
    private UUID staffId;

    @Embedded
    private FullName identity;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Embedded
    private OrganisationJpa organisation;

    @Embedded
    private PlacementJpa placement;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmploymentStatus employmentStatus;

    @Column(name="identityId")
    private String identityId;
}
