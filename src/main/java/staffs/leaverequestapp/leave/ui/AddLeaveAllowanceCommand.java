package staffs.leaverequestapp.leave.ui;

import staffs.leaverequestapp.common.domain.FullName;

import java.time.LocalDate;
import java.util.UUID;

public record AddLeaveAllowanceCommand(
        UUID staffId,
        UUID managerId,
        int year,
        double totalAllowance,
        FullName fullName)
{
//validation
}