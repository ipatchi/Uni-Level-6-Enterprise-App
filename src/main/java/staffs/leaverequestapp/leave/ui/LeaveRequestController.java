package staffs.leaverequestapp.leave.ui;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import staffs.leaverequestapp.leave.LeaveContextFacade;
import staffs.leaverequestapp.leave.application.dto.LeaveRequestDTO;

import java.util.UUID;

@RequestMapping("/leave-requests")
@RestController
@AllArgsConstructor
public class LeaveRequestController {
    private final LeaveContextFacade facade;

    @GetMapping("/{staff_id}")
    @ResponseStatus(HttpStatus.OK)
    public Iterable<LeaveRequestDTO> getLeaveRequestsByStaffId(@PathVariable UUID staff_id) {
        return facade.findLeaveRequestsByStaffId(staff_id);
    }

    @GetMapping("/manager/{manager_id}")
    @ResponseStatus(HttpStatus.OK)
    public Iterable<LeaveRequestDTO> getLeaveRequestsByManager(@PathVariable UUID manager_id) {
        return facade.findLeaveRequestsByManagerId(manager_id);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public void createLeaveRequest(@RequestBody SubmitLeaveRequestCommand command) {
        facade.makeLeaveRequest(command);
    }

    @PatchMapping("/{request_id}/approve")
    @ResponseStatus(HttpStatus.OK)
    public void approveLeaveRequest(@RequestBody ApproveLeaveRequestCommand command) {
        facade.approveLeaveRequest(command);
    }

    @PatchMapping("/{request_id}/reject")
    @ResponseStatus(HttpStatus.OK)
    public void rejectLeaveRequest(@RequestBody RejectLeaveRequestCommand command) {
        facade.rejectLeaveRequest(command);
    }



}
