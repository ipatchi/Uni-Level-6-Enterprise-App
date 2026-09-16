package staffs.leaverequestapp.leave.domain;

import lombok.Getter;
import lombok.ToString;
import staffs.leaverequestapp.common.domain.AggregateRoot;
import staffs.leaverequestapp.common.domain.Identity;
import staffs.leaverequestapp.leave.domain.exceptions.LeaveRequestCannotBeCancelledByProxyException;
import staffs.leaverequestapp.leave.domain.exceptions.LeaveRequestCannotBeRejectedException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static staffs.leaverequestapp.common.domain.DomainAssertions.argumentNotEmpty;

@ToString(callSuper = true)
public class LeaveRequest extends AggregateRoot<LeaveRequest> {
    private UUID staffId;
    private LocalDate startDate;
    private LocalDate endDate;
    private double totalDays;
    private String reason;
    private LeaveStatus status;

    // The Factory Constructor
    public LeaveRequest(Identity<LeaveRequest> id,
                        UUID staffId,
                        LocalDate startDate,
                        LocalDate endDate,
                        String reason) {
        super(id);

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        this.staffId = staffId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = argumentNotEmpty(reason, "Reason cannot be empty");
        this.status = LeaveStatus.PENDING;

        this.totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;

    }

    public UUID getStaffId() {return staffId;}
    public LocalDate getStartDate() {return startDate;}
    public LocalDate getEndDate() {return endDate;}
    public String getReason() {return reason;}
    public LeaveStatus getStatus() {return status;}
    public double getTotalDays() {return totalDays;}


    public static LeaveRequest leaveRequestOf(Identity<LeaveRequest> id,
                                  UUID staffId,
                                  LocalDate startDate,
                                  LocalDate endDate,
                                  double totalDays,
                                  String reason,
                                  LeaveStatus status) {
        LeaveRequest leaveRequest = new LeaveRequest(id, staffId, startDate, endDate, reason);
        leaveRequest.status = status;
        leaveRequest.totalDays = totalDays;
        return leaveRequest;
    }

//ACTIONS!

    public void approveRequest() {
        if (status == LeaveStatus.PENDING) {
            status = LeaveStatus.APPROVED;
        }
    }

    public void rejectRequest() {
        switch (status) {
            case PENDING:
                status = LeaveStatus.REJECTED;
                break;
            case APPROVED:
                throw new LeaveRequestCannotBeRejectedException("Approved requests cannot be rejected");
            default:
        }
    }

    public void cancelRequest(UUID staffIdRequestingCancel) {
        if (!this.staffId.equals(staffIdRequestingCancel)) {
            throw new LeaveRequestCannotBeCancelledByProxyException("You can only cancel your own leave requests.");
        }
        this.status = LeaveStatus.CANCELLED;
    }
}
