package staffs.leaverequestapp.leave.ui;

import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import staffs.leaverequestapp.leave.LeaveContextFacade;
import staffs.leaverequestapp.leave.application.dto.LeaveRequestDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RequestMapping("/leave-requests")
@RestController
@AllArgsConstructor
public class LeaveRequestController {
    private final LeaveContextFacade facade;

    //Leave Requests for a specific staff ID
    @GetMapping("/{staff_id}")
    @ResponseStatus(HttpStatus.OK)
    public Iterable<LeaveRequestDTO> getLeaveRequestsByStaffId(@PathVariable UUID staff_id) {
        return facade.findLeaveRequestsByStaffId(staff_id);
    }

    //Leave requests for the manager making request, filtered by dates
    @GetMapping("/team")
    public ResponseEntity<List<LeaveRequestDTO>> getTeamLeaveRequests(
            @RequestHeader("X-User-Id") UUID managerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<LeaveRequestDTO> requests = facade.findTeamLeaveOutstandingRequests(managerId, startDate, endDate);
        return ResponseEntity.ok(requests);
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

    //Create new leave request
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public String createLeaveRequest(@RequestBody SubmitLeaveRequestCommand command) {
        return facade.makeLeaveRequest(command);

    }

    //Approve a specific leave request (as manager)
    @PatchMapping("{leaveRequestId}/approve")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String approveLeaveRequest(@PathVariable String leaveRequestId, @RequestHeader("X-User-Id") UUID managerId) {
        ApproveLeaveRequestCommand command = new ApproveLeaveRequestCommand(managerId, leaveRequestId);
        return facade.approveLeaveRequest(command);
    }

    //Reject a specific leave request (as manager)
    @PatchMapping("{leaveRequestId}/reject")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String rejectLeaveRequest(@PathVariable String leaveRequestId, @RequestHeader("X-User-Id") UUID managerId) {
        RejectLeaveRequestCommand command = new RejectLeaveRequestCommand(managerId, leaveRequestId);
        return facade.rejectLeaveRequest(command);
    }

    //Cancel a specific leave request (as self)
    @PatchMapping("/{leaveRequestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String cancelLeaveRequest(@PathVariable String leaveRequestId, @RequestHeader("X-User-Id") UUID staffId) {
        CancelLeaveRequestCommand command = new CancelLeaveRequestCommand(staffId, leaveRequestId);
        return facade.cancelLeaveRequest(command);
    }




}
