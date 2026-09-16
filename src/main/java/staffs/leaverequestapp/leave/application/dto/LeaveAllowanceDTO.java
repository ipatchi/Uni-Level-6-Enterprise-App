package staffs.leaverequestapp.leave.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import staffs.leaverequestapp.common.domain.FullName;

import java.util.Objects;
import java.util.UUID;

public record LeaveAllowanceDTO(
        UUID id,
        UUID staffId,
        String firstName,
        String surname,
        UUID managerId,
        int year,
        double totalAllowance,
        double usedAllowance
) {
    public LeaveAllowanceDTO {
        Objects.requireNonNull(id, "ID cannot be null");
        Objects.requireNonNull(staffId, "Staff ID cannot be null");
        Objects.requireNonNull(firstName, "First name cannot be null");
        Objects.requireNonNull(surname, "Surname cannot be null");
        Objects.requireNonNull(managerId, "Manager ID cannot be null");

        if (year < 2000) {
            throw new IllegalArgumentException("Year must be valid");
        }

        if (totalAllowance < 0) {
            throw new IllegalArgumentException("Total allowance cannot be negative");
        }
        if (usedAllowance < 0) {
            throw new IllegalArgumentException("Used allowance cannot be negative");
        }
        if (usedAllowance > totalAllowance) {
            throw new IllegalArgumentException("Used allowance cannot exceed total allowance");
        }
    }

    @JsonProperty("remainingAllowance")
    public double remainingAllowance() {
        return totalAllowance - usedAllowance;
    }
}