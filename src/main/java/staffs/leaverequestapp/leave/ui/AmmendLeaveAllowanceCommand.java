package staffs.leaverequestapp.leave.ui;

import java.util.UUID;

public record AmmendLeaveAllowanceCommand(
        String identityId,
        UUID staffId,
        double totalAllowance)
{
//validation
}