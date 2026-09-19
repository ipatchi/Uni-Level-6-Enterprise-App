package staffs.leaverequestapp.leave.domain.exceptions;

public class LeaveRequestCannotBeRejectedException  extends RuntimeException {
    public LeaveRequestCannotBeRejectedException(String request_id) {
        super(request_id);
    }
}
