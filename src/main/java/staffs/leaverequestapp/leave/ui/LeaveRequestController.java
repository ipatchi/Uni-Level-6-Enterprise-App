package staffs.leaverequestapp.leave.ui;

import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import staffs.leaverequestapp.leave.LeaveContextFacade;
import staffs.leaverequestapp.leave.application.dto.LeaveRequestDTO;

import java.security.Principal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RequestMapping("/leave-requests")
@RestController
@AllArgsConstructor
public class LeaveRequestController {
    private final LeaveContextFacade facade;

    //View own leave requests
    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public Iterable<LeaveRequestDTO> getLeaveRequestsByStaffId(Principal principle) {
        return facade.findMyLeaveRequests(principle.getName());
    }

    //View my team (as manager) with filters
    @GetMapping("/team")
    public ResponseEntity<List<LeaveRequestDTO>> getTeamLeaveRequests(
            Principal principal,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<LeaveRequestDTO> requests = facade.findTeamLeaveOutstandingRequests(principal.getName(), startDate, endDate);
        return ResponseEntity.ok(requests);
    }

    //Create new leave request
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public String createLeaveRequest(@RequestBody SubmitLeaveRequestCommand command, Principal principal) {
        SubmitLeaveRequestCommand authenticatedCommand = new SubmitLeaveRequestCommand(
                principal.getName(),
                command.startDate(),
                command.endDate(),
                command.reason()
        );
        return facade.makeLeaveRequest(authenticatedCommand);

    }

    //Approve a specific leave request (as manager)
    @PatchMapping("{leaveRequestId}/approve")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String approveLeaveRequest(@PathVariable String leaveRequestId,
                                      Principal principal) {
        return facade.approveLeaveRequest(
                new ApproveLeaveRequestCommand(principal.getName(), leaveRequestId,
                        isAdmin(principal))
        );
    }

    //Reject a specific leave request (as manager)
    @PatchMapping("{leaveRequestId}/reject")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String rejectLeaveRequest(@PathVariable String leaveRequestId,
                                     Principal principal) {
        return facade.rejectLeaveRequest(
                new RejectLeaveRequestCommand(principal.getName(), leaveRequestId,
                        isAdmin(principal))
        );
    }

    private boolean isAdmin(Principal principal) {
        Authentication authentication = principal instanceof Authentication principalAuthentication
                ? principalAuthentication
                : SecurityContextHolder.getContext().getAuthentication();

        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }

    //Cancel a specific leave request (as self)
    @PatchMapping("/{leaveRequestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String cancelLeaveRequest(@PathVariable String leaveRequestId, Principal principal) {
        return facade.cancelLeaveRequest(
                new CancelLeaveRequestCommand(principal.getName(), leaveRequestId)
        );
    }


    //Get admin view of leave requests with filters
    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public Iterable<LeaveRequestDTO> getFilteredLeaveRequests(
            @RequestParam(required = false) UUID staffId,
            @RequestParam(required = false) UUID managerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return facade.findFilteredOutstandingLeaveRequests(staffId, managerId, startDate, endDate);
    }


}
