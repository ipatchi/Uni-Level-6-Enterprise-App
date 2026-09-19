package staffs.leaverequestapp.leave.domain.exceptions;

public class LeaveRequestCannotBeApprovedException extends RuntimeException {
    public LeaveRequestCannotBeApprovedException(String request_id) {super(request_id);}
}
