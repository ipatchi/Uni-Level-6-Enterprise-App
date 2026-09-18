package staffs.leaverequestapp.leave;

import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import staffs.leaverequestapp.leave.application.LeaveAllowanceQueryHandler;
import staffs.leaverequestapp.leave.application.LeaveRequestApplicationService;
import staffs.leaverequestapp.leave.application.LeaveRequestQueryHandler;
import staffs.leaverequestapp.leave.application.dto.LeaveAllowanceDTO;
import staffs.leaverequestapp.leave.application.dto.LeaveRequestDTO;
import staffs.leaverequestapp.leave.ui.ApproveLeaveRequestCommand;
import staffs.leaverequestapp.leave.ui.RejectLeaveRequestCommand;
import staffs.leaverequestapp.leave.ui.SubmitLeaveRequestCommand;
//import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class LeaveContextFacade {
    private final LeaveRequestQueryHandler leaveRequestQueryHandler;
    private final LeaveAllowanceQueryHandler leaveAllowanceQueryHandler;
    private final LeaveRequestApplicationService leaveRequestApplicationService;

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
    public void makeLeaveRequest(SubmitLeaveRequestCommand submitLeaveRequestCommand){
        leaveRequestApplicationService.submitLeaveRequest(submitLeaveRequestCommand);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public void approveLeaveRequest(ApproveLeaveRequestCommand approveLeaveRequestCommand){
        leaveRequestApplicationService.approveLeaveRequest(approveLeaveRequestCommand);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public void rejectLeaveRequest(RejectLeaveRequestCommand rejectLeaveRequestCommand){
        leaveRequestApplicationService.rejectLeaveRequest(rejectLeaveRequestCommand);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public LeaveAllowanceDTO findLeaveAllowanceByUserId(UUID staffId) {
        return leaveAllowanceQueryHandler.findLeaveAllowanceByUserId(staffId);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public List<LeaveAllowanceDTO> findLeaveAllowanceByManagerId(UUID managerId) {
        return leaveAllowanceQueryHandler.findLeaveAllowanceByManagerId(managerId);
    }
}
