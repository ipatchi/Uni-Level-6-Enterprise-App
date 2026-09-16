package staffs.leaverequestapp.leave.ui;

import java.util.UUID;

public record RejectLeaveRequestCommand(
        UUID managerId,
        String leaveRequestId
)
{
//validation
}
