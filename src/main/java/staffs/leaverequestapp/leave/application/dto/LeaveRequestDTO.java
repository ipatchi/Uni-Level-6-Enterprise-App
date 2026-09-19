package staffs.leaverequestapp.leave.application.dto;

import staffs.leaverequestapp.leave.domain.LeaveStatus;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;

public record LeaveRequestDTO(
        String id,
        UUID staffId,
        LocalDate startDate,
        LocalDate endDate,
        double totalDays,
        String reason,
        LeaveStatus status
) {
    public LeaveRequestDTO {
        Objects.requireNonNull(id, "ID cannot be null");
        Objects.requireNonNull(staffId, "Staff ID cannot be null");
        Objects.requireNonNull(startDate, "Start date cannot be null");
        Objects.requireNonNull(endDate, "End date cannot be null");
        Objects.requireNonNull(reason, "Reason cannot be null");
        Objects.requireNonNull(status, "Status cannot be null");

        if (totalDays <= 0) {
            throw new IllegalArgumentException("Total days must be greater than zero");
        }
    }
}
