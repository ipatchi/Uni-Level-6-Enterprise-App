package staffs.leaverequestapp.leave.domain.exceptions;

public class LeaveRequestCannotBeCancelledByProxyException extends RuntimeException {
    public LeaveRequestCannotBeCancelledByProxyException(String order_id) {
        super(order_id);
    }
}
