package staffs.leaverequestapp.leave.ui;


import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import staffs.leaverequestapp.leave.LeaveContextFacade;
import staffs.leaverequestapp.leave.application.dto.LeaveAllowanceDTO;
import staffs.leaverequestapp.leave.application.mapper.LeaveAllowanceJpaToDTOMapper;
import staffs.leaverequestapp.leave.persistance.entities.LeaveAllowanceJpa;
import staffs.leaverequestapp.staff.ui.CreateStaffMemberCommand;

import java.util.UUID;

@RequestMapping("/leave-allowance")
@RestController
@AllArgsConstructor
public class LeaveAllowanceController {
    private final LeaveContextFacade facade;

    @GetMapping("/{staff_id}")
    @ResponseStatus(HttpStatus.OK)
    public LeaveAllowanceDTO getLeaveAllowanceByStaffId(@PathVariable UUID staff_id) {
        return facade.findLeaveAllowanceByUserId(staff_id);
    }

    @GetMapping("/manager/{manager_id}")
    @ResponseStatus(HttpStatus.OK)
    public Iterable<LeaveAllowanceDTO> getLeaveAllowancesByManager(@PathVariable UUID manager_id) {
        return facade.findLeaveAllowanceByManagerId(manager_id);
    }

    @PatchMapping("/edit")
    @ResponseStatus(HttpStatus.OK)
    public LeaveAllowanceDTO editLeaveAllowance(@RequestBody AmmendLeaveAllowanceCommand command) {
        LeaveAllowanceJpa updatedAllowanceJpa = facade.editLeaveAllowance(command);
        return LeaveAllowanceJpaToDTOMapper.toLeaveAllowanceDTO(updatedAllowanceJpa);
    }
}

