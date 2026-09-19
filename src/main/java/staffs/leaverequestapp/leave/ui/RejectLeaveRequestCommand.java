package staffs.leaverequestapp.leave.ui;

public record RejectLeaveRequestCommand(
        String identityId,
        String leaveRequestId,
        boolean adminOverride
)
{
    public RejectLeaveRequestCommand(String identityId, String leaveRequestId) {
        this(identityId, leaveRequestId, false);
    }
}
