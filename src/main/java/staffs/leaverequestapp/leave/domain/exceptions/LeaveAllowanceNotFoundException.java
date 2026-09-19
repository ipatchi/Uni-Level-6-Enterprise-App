package staffs.leaverequestapp.leave.domain.exceptions;

public class LeaveAllowanceNotFoundException extends RuntimeException {
    public LeaveAllowanceNotFoundException(String alllowance_id) {
        super(alllowance_id);
    }
}