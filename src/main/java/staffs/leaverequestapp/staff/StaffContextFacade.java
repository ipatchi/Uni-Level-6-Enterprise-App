package staffs.leaverequestapp.staff;

import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import staffs.leaverequestapp.staff.application.StaffApplicationService;
import staffs.leaverequestapp.staff.application.StaffQueryHandler;
import staffs.leaverequestapp.staff.application.dto.StaffDTO;
import staffs.leaverequestapp.staff.ui.AmmendStaffMemberCommand;
import staffs.leaverequestapp.staff.ui.CreateStaffMemberCommand;

import java.util.UUID;

@Component
@AllArgsConstructor
public class StaffContextFacade {
    private final StaffApplicationService staffApplicationService;
    private final StaffQueryHandler staffQueryHandler;

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public UUID createStaffMember(CreateStaffMemberCommand command) {
        return staffApplicationService.createStaffMember(command);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public StaffDTO getStaffById(@PathVariable UUID staffId) {
        return staffQueryHandler.getStaffById(staffId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void amendStaffMember(AmmendStaffMemberCommand command) {
        staffApplicationService.ammendStaffMember(command);
    }
}
