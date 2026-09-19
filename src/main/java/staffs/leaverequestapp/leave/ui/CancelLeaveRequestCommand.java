package staffs.leaverequestapp.leave.ui;

public record CancelLeaveRequestCommand(
        String identityId,
        String leaveRequestId
)
{
}
