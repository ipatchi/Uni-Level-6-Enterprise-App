package staffs.leaverequestapp.leave.application.mapper;

import staffs.leaverequestapp.common.domain.Identity;
import staffs.leaverequestapp.leave.domain.LeaveRequest;
import staffs.leaverequestapp.leave.persistance.entities.LeaveRequestJpa;

import java.time.temporal.ChronoUnit;

public class LeaveRequestJpaToDomainMapper {

    public static LeaveRequest map(LeaveRequestJpa leaveRequestJpa) {
        if (leaveRequestJpa == null) return null;

        Identity<LeaveRequest> id = Identity.of(leaveRequestJpa.getId());

        long totalDays = ChronoUnit.DAYS.between(leaveRequestJpa.getStartDate(), leaveRequestJpa.getEndDate()) + 1;

        return LeaveRequest.leaveRequestOf(
                id,
                leaveRequestJpa.getStaffId(),
                leaveRequestJpa.getStartDate(),
                leaveRequestJpa.getEndDate(),
                leaveRequestJpa.getTotalDays(),
                leaveRequestJpa.getReason(),
                leaveRequestJpa.getStatus()
        );
    }
}