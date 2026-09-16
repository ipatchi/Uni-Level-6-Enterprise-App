package staffs.leaverequestapp.leave.ui;

import java.time.LocalDate;
import java.util.UUID;

public record ApproveLeaveRequestCommand(
        UUID managerId,
        String leaveRequestId
)
{
//validation
}
