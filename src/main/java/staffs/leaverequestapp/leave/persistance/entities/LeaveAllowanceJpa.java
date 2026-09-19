package staffs.leaverequestapp.leave.persistance.entities;


import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import staffs.leaverequestapp.common.domain.FullName;
import staffs.leaverequestapp.leave.domain.LeaveStatus;

import java.time.LocalDate;
import java.util.UUID;

@Entity(name = "leave_allowance")
@Table(name ="leave_allowances")
@Getter
@Setter
@ToString
public class LeaveAllowanceJpa {
    @Id
    @Column(name="id")
    private String id;

    @NotNull(message = "Staff ID is required")
    @Column(name = "staff_id")
    private UUID staffId;

    @Embedded
    @Valid
    private FullName fullName;

    @NotNull(message = "Manager id is required")
    @Column(name = "manager_id")
    private UUID managerId;

    @NotNull(message = "Year is required")
    @Column(name = "allowance_year")
    private int year;

    @Column(name = "total_allowance")
    private double totalAllowance;

    @Column(name = "used_allowance")
    private double usedAllowance;

    @Column(name = "identityId")
    private String identityId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LeaveRequestJpa)) return false;
        LeaveRequestJpa other = (LeaveRequestJpa) o;

        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
