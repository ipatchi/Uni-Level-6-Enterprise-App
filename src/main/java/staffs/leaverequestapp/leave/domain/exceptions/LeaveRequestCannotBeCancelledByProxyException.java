package staffs.leaverequestapp.leave.domain.exceptions;

public class LeaveRequestCannotBeCancelledByProxyException extends RuntimeException {
    public LeaveRequestCannotBeCancelledByProxyException(String request_id) {
        super(request_id);
    }
}
