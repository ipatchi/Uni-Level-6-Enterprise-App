package staffs.leaverequestapp.leave.domain.exceptions;

public class LeaveRequestCannotBeRejectedException  extends RuntimeException {
    public LeaveRequestCannotBeRejectedException(String order_id) {
        super(order_id);
    }
}
