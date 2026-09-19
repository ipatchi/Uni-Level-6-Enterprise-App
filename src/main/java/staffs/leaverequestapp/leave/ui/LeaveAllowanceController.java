package staffs.leaverequestapp.leave.ui;


import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import staffs.leaverequestapp.leave.LeaveContextFacade;
import staffs.leaverequestapp.leave.application.dto.LeaveAllowanceDTO;

import java.security.Principal;

@RequestMapping("/leave-allowance")
@RestController
@AllArgsConstructor
public class LeaveAllowanceController {
    private final LeaveContextFacade facade;

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public LeaveAllowanceDTO getMyLeaveAllowance(Principal principal) {
        return facade.findMyLeaveAllowance(principal.getName());
    }

    @GetMapping("/team")
    @ResponseStatus(HttpStatus.OK)
    public Iterable<LeaveAllowanceDTO> getMyTeamLeaveAllowances(Principal principal) {
        return facade.findMyTeamLeaveAllowances(principal.getName());
    }

    @PatchMapping("/edit")
    @ResponseStatus(HttpStatus.OK)
    public LeaveAllowanceDTO editLeaveAllowance(
            @RequestBody AmmendLeaveAllowanceCommand command,
            Principal principal
    ) {
        AmmendLeaveAllowanceCommand authenticatedCommand = new AmmendLeaveAllowanceCommand(
                principal.getName(),
                command.staffId(),
                command.totalAllowance()
        );
        return facade.editLeaveAllowance(authenticatedCommand);
    }
}
