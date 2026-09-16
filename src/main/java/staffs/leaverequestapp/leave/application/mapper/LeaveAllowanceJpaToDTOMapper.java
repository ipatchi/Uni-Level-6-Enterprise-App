package staffs.leaverequestapp.leave.application.mapper;

import staffs.leaverequestapp.leave.application.dto.LeaveAllowanceDTO;
import staffs.leaverequestapp.leave.persistance.entities.LeaveAllowanceJpa;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class LeaveAllowanceJpaToDTOMapper {

    public static LeaveAllowanceDTO toLeaveAllowanceDTO (LeaveAllowanceJpa leaveAllowance) {
        Objects.requireNonNull(leaveAllowance, "Leave allowance JPA entity cannot be null");
        Objects.requireNonNull(leaveAllowance.getFullName(), "Full name cannot be null");

        UUID id = leaveAllowance.getId() != null ? UUID.fromString(leaveAllowance.getId()) : null;

        return new LeaveAllowanceDTO(
                id,
                leaveAllowance.getStaffId(),
                leaveAllowance.getFullName().firstName(),
                leaveAllowance.getFullName().surname(),
                leaveAllowance.getManagerId(),
                leaveAllowance.getYear(),
                leaveAllowance.getTotalAllowance(),
                leaveAllowance.getUsedAllowance()
        );
    }
}