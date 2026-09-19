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
import staffs.leaverequestapp.leave.application.mapper.LeaveAllowanceJpaToDTOMapper;
import staffs.leaverequestapp.leave.ui.*;

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

    //Find own leave requests
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public List<LeaveRequestDTO> findMyLeaveRequests(String identityId) {
        UUID staffId = leaveAllowanceQueryHandler.findLeaveAllowanceByIdentityId(identityId).staffId();
        return leaveRequestQueryHandler.findLeaveRequestsByStaffId(staffId);
    }

    //Find teams leave requests (outstanding) and filter by dates
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<LeaveRequestDTO> findTeamLeaveOutstandingRequests(String identityId, LocalDate startDate, LocalDate endDate) {
        UUID managerId = leaveAllowanceQueryHandler.findLeaveAllowanceByIdentityId(identityId).staffId();
        List<LeaveAllowanceDTO> teamAllowances = leaveAllowanceQueryHandler.findLeaveAllowanceByManagerId(managerId);

        List<UUID> staffIds = teamAllowances.stream()
                .map(LeaveAllowanceDTO::staffId)
                .toList();

        return leaveRequestQueryHandler.findOutstandingLeaveRequestsByStaffIdsAndDates(staffIds, startDate, endDate);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public String makeLeaveRequest(SubmitLeaveRequestCommand command) {
        return leaveRequestApplicationService.submitLeaveRequest(command);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public String approveLeaveRequest(ApproveLeaveRequestCommand command) {
        return leaveRequestApplicationService.approveLeaveRequest(command);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public String rejectLeaveRequest(RejectLeaveRequestCommand command) {
        return leaveRequestApplicationService.rejectLeaveRequest(command);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER', 'STAFF')")
    public String cancelLeaveRequest(CancelLeaveRequestCommand command) {
        return leaveRequestApplicationService.cancelLeaveRequest(command);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public LeaveAllowanceDTO findLeaveAllowanceByUserId(UUID staffId) {
        return leaveAllowanceQueryHandler.findLeaveAllowanceByUserId(staffId);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public LeaveAllowanceDTO findMyLeaveAllowance(String identityId) {
        return leaveAllowanceQueryHandler.findLeaveAllowanceByIdentityId(identityId);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public List<LeaveAllowanceDTO> findLeaveAllowanceByManagerId(UUID managerId) {
        return leaveAllowanceQueryHandler.findLeaveAllowanceByManagerId(managerId);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<LeaveAllowanceDTO> findMyTeamLeaveAllowances(String identityId) {
        UUID managerId = leaveAllowanceQueryHandler
                .findLeaveAllowanceByIdentityId(identityId)
                .staffId();
        return leaveAllowanceQueryHandler.findLeaveAllowanceByManagerId(managerId);
    }


    @PreAuthorize("hasAnyRole('ADMIN')")
    public LeaveAllowanceDTO editLeaveAllowance(AmmendLeaveAllowanceCommand command) {
        return LeaveAllowanceJpaToDTOMapper.toLeaveAllowanceDTO(
                leaveAllowanceApplicationService.ammendLeaveAllowance(command));
    }

    @PreAuthorize("hasRole('ADMIN')")
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
