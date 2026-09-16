package staffs.leaverequestapp.leave.application.mapper;

import staffs.leaverequestapp.leave.application.dto.LeaveRequestDTO;
import staffs.leaverequestapp.leave.persistance.entities.LeaveRequestJpa;

import java.util.Objects;

public class LeaveRequestJpaToDTOMapper {

    public static LeaveRequestDTO toLeaveRequestDTO(LeaveRequestJpa leaveRequest) {
        Objects.requireNonNull(leaveRequest, "Leave request JPA entity cannot be null");

        return new LeaveRequestDTO(
                leaveRequest.getId(),
                leaveRequest.getStaffId(),
                leaveRequest.getStartDate(),
                leaveRequest.getEndDate(),
                leaveRequest.getTotalDays(),
                leaveRequest.getReason(),
                leaveRequest.getStatus()
        );
    }
}