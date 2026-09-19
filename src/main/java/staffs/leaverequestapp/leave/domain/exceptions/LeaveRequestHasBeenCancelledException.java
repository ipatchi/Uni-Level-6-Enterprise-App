package staffs.leaverequestapp.leave.domain.exceptions;

public class LeaveRequestHasBeenCancelledException extends RuntimeException {
    public LeaveRequestHasBeenCancelledException(String message) {
        super(message);
    }
}
