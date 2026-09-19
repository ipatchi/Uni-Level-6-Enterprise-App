package staffs.leaverequestapp.staff.ui;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import staffs.leaverequestapp.staff.StaffContextFacade;
import staffs.leaverequestapp.staff.application.dto.StaffDTO;

import java.util.UUID;

@RestController
@RequestMapping("/staff")
@AllArgsConstructor
public class StaffMemberController {
    private final StaffContextFacade facade;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public UUID createStaffMember(@RequestBody CreateStaffMemberCommand command) {
        return facade.createStaffMember(command);
    }

    @PatchMapping("/admin/{staffId}/edit")
    @ResponseStatus(HttpStatus.OK)
    public void appendStaffMember(@PathVariable UUID staffId, @RequestBody AmmendStaffMemberCommand command ) {
        facade.amendStaffMember(command.withStaffId(staffId));
    }

    @GetMapping("/{staffId}")
    @ResponseStatus(HttpStatus.OK)
    public StaffDTO getStaffMemberById(@PathVariable UUID staffId) {
        return facade.getStaffById(staffId);
    }

    @GetMapping("/identity/{identityId}")
    @ResponseStatus(HttpStatus.OK)
    public StaffDTO getStaffMemberByIdentityId(@PathVariable String identityId) {
        return facade.getStaffByIdentityId(identityId);
    }
}
