package staffs.leaverequestapp.leave.ui;

public record ApproveLeaveRequestCommand(
        String identityId,
        String leaveRequestId,
        boolean adminOverride
)
{
    public ApproveLeaveRequestCommand(String identityId, String leaveRequestId) {
        this(identityId, leaveRequestId, false);
    }
}
