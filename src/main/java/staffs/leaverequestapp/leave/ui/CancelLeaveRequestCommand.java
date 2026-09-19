package staffs.leaverequestapp.leave.ui;

import java.util.UUID;

public record CancelLeaveRequestCommand(
        UUID staffId,
        String leaveRequestId
)
{
}
