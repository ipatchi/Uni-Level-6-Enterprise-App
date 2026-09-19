package staffs.leaverequestapp.leave.ui;

import java.util.UUID;

public record AmmendLeaveAllowanceCommand(
        UUID staffId,
        double totalAllowance)
{
//validation
}