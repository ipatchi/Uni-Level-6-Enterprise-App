package staffs.leaverequestapp.leave.ui;

import staffs.leaverequestapp.common.domain.FullName;

import java.util.UUID;

public record AmmendLeaveAllowanceDetailsCommand(
        UUID staffId,
        String newFirstName,
        String newSurname,
        UUID newManagerId)
{
//validation
}