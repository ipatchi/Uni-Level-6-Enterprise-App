package staffs.leaverequestapp.leave.application.mapper;

import staffs.leaverequestapp.leave.domain.LeaveRequest;
import staffs.leaverequestapp.leave.persistance.entities.LeaveRequestJpa;

import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;

public class LeaveRequestDomainToJpaMapper {

    public static LeaveRequestJpa map(LeaveRequest request) {
        if (request == null) return null;

        LeaveRequestJpa leaveRequestJpa = new LeaveRequestJpa();
        leaveRequestJpa.setId(request.id().id());
        leaveRequestJpa.setStaffId(request.getStaffId());
        leaveRequestJpa.setStartDate(request.getStartDate());
        leaveRequestJpa.setEndDate(request.getEndDate());
        leaveRequestJpa.setReason(request.getReason());
        leaveRequestJpa.setStatus(request.getStatus());
        leaveRequestJpa.setTotalDays(ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1);

        return leaveRequestJpa;
    }
}