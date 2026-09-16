package staffs.leaverequestapp.leave.persistance.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import staffs.leaverequestapp.leave.domain.LeaveStatus;

import java.time.LocalDate;
import java.util.UUID;

@Entity(name = "leave_request")
@Table(name ="leave_requests")
@Getter
@Setter
@ToString
public class LeaveRequestJpa {
    @Id
    @Column(name="id")
    private String id;

    @NotNull(message = "Staff ID is required")
    @Column(name = "staff_id")
    private UUID staffId;

    @NotNull(message = "Start date is required")
    @Column(name = "start_date")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @Column(name = "end_date")
    private LocalDate endDate;

    @NotNull(message = "Total days is required")
    @Column(name = "total_days")
    private double totalDays;

    @NotBlank(message = "Reason is required")
    @Column(name = "reason")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private LeaveStatus status;

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
