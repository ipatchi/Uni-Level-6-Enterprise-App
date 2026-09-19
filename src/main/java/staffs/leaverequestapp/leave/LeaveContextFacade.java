package staffs.leaverequestapp.leave;

import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import staffs.leaverequestapp.leave.application.LeaveAllowanceApplicationService;
import staffs.leaverequestapp.leave.application.LeaveAllowanceQueryHandler;
import staffs.leaverequestapp.leave.application.LeaveRequestApplicationService;
import staffs.leaverequestapp.leave.application.LeaveRequestQueryHandler;
import staffs.leaverequestapp.leave.application.dto.LeaveAllowanceDTO;
import staffs.leaverequestapp.leave.application.dto.LeaveRequestDTO;
import staffs.leaverequestapp.leave.persistance.entities.LeaveAllowanceJpa;
import staffs.leaverequestapp.leave.ui.*;
//import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class LeaveContextFacade {
    private final LeaveRequestQueryHandler leaveRequestQueryHandler;
    private final LeaveAllowanceQueryHandler leaveAllowanceQueryHandler;
    private final LeaveRequestApplicationService leaveRequestApplicationService;
    private final LeaveAllowanceApplicationService leaveAllowanceApplicationService;

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public List<LeaveRequestDTO> findLeaveRequestsByStaffId(UUID staffId) {
        return leaveRequestQueryHandler.findLeaveRequestsByStaffId(staffId);
    }

   @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<LeaveRequestDTO> findLeaveRequestsByManagerId(UUID managerId) {
        List<LeaveAllowanceDTO> teamAllowances = leaveAllowanceQueryHandler.findLeaveAllowanceByManagerId(managerId);

        List<UUID> staffIds = teamAllowances.stream()
                .map(LeaveAllowanceDTO::staffId)
                .toList();

        return leaveRequestQueryHandler.findLeaveRequestsByStaffIds(staffIds);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public String makeLeaveRequest(SubmitLeaveRequestCommand submitLeaveRequestCommand){
        return leaveRequestApplicationService.submitLeaveRequest(submitLeaveRequestCommand);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public String approveLeaveRequest(ApproveLeaveRequestCommand approveLeaveRequestCommand){
        return leaveRequestApplicationService.approveLeaveRequest(approveLeaveRequestCommand);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public String rejectLeaveRequest(RejectLeaveRequestCommand rejectLeaveRequestCommand){
        return leaveRequestApplicationService.rejectLeaveRequest(rejectLeaveRequestCommand);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER', 'STAFF')")
    public String cancelLeaveRequest(CancelLeaveRequestCommand cancelLeaveRequestCommand){
        return leaveRequestApplicationService.cancelLeaveRequest(cancelLeaveRequestCommand);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public LeaveAllowanceDTO findLeaveAllowanceByUserId(UUID staffId) {
        return leaveAllowanceQueryHandler.findLeaveAllowanceByUserId(staffId);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public List<LeaveAllowanceDTO> findLeaveAllowanceByManagerId(UUID managerId) {
        return leaveAllowanceQueryHandler.findLeaveAllowanceByManagerId(managerId);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<LeaveRequestDTO> findTeamLeaveOutstandingRequests(UUID managerId, LocalDate startDate, LocalDate endDate) {
        List<LeaveAllowanceDTO> teamAllowances = leaveAllowanceQueryHandler.findLeaveAllowanceByManagerId(managerId);

        List<UUID> staffIds = teamAllowances.stream()
                .map(LeaveAllowanceDTO::staffId)
                .toList();

        return leaveRequestQueryHandler.findOutstandingLeaveRequestsByStaffIdsAndDates(staffIds, startDate, endDate);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    public LeaveAllowanceJpa editLeaveAllowance(AmmendLeaveAllowanceCommand command) {
        return leaveAllowanceApplicationService.ammendLeaveAllowance(command);
    }

    public List<LeaveRequestDTO> findFilteredOutstandingLeaveRequests(UUID staffId, UUID managerId, LocalDate startDate, LocalDate endDate) {
        if (staffId != null) {
            return leaveRequestQueryHandler.findOutstandingLeaveRequestsByStaffIdsAndDates(
                    List.of(staffId), startDate, endDate
            );
        }

        if (managerId != null) {
            List<LeaveAllowanceDTO> teamAllowances = leaveAllowanceQueryHandler.findLeaveAllowanceByManagerId(managerId);
            List<UUID> teamStaffIds = teamAllowances.stream()
                    .map(LeaveAllowanceDTO::staffId)
                    .toList();
            if (teamStaffIds.isEmpty()) {
                return List.of();
            }
            return leaveRequestQueryHandler.findOutstandingLeaveRequestsByStaffIdsAndDates(
                    teamStaffIds, startDate, endDate
            );
        };
        return leaveRequestQueryHandler.findAllOutstandingLeaveRequestsByDates(startDate, endDate);


    }
}
