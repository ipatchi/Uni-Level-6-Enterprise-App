package staffs.leaverequestapp.leave.domain;

import staffs.leaverequestapp.common.domain.AggregateRoot;
import staffs.leaverequestapp.common.domain.FullName;
import staffs.leaverequestapp.common.domain.Identity;
import staffs.leaverequestapp.leave.domain.exceptions.InsufficientLeaveBalanceException;

import java.util.UUID;

public class LeaveAllowance extends AggregateRoot<LeaveAllowance> {

    private UUID staffId;
    private FullName fullName;
    private UUID managerId;
    private int year;
    private double totalAllowance;
    private double usedAllowance;

    public LeaveAllowance(
            Identity<LeaveAllowance> id,
            UUID staffId,
            UUID managerId,
            int year,
            double totalAllowance,
            FullName fullName
    ) {
        super(id);

        if (totalAllowance <= 0) {
            throw new IllegalArgumentException("Total allowance must be greater than zero");
        }

        this.staffId = staffId;
        this.managerId = managerId;
        this.year = year;
        this.totalAllowance = totalAllowance;
        this.usedAllowance = 0.0;
        this.fullName = fullName;
    }

    public UUID getStaffId() {return staffId;}
    public int getYear() {return year;}
    public double getTotalAllowance() {return totalAllowance;}
    public double getUsedAllowance() {return usedAllowance;}
    public FullName getFullName() {return fullName;}
    public UUID getManagerId() {return managerId;}

    public static LeaveAllowance leaveAllowanceOf(
            Identity<LeaveAllowance> id,
            UUID staffId,
            FullName fullName,
            UUID managerId,
            int year,
            double totalAllowance,
            double usedAllowance
    ) {
        LeaveAllowance leaveAllowance = new LeaveAllowance(id, staffId, managerId, year, totalAllowance, fullName);
        leaveAllowance.usedAllowance = usedAllowance;
        return leaveAllowance;
    }


    //Actions!

    public void deductLeave(double daysToDeduct) {
        if (daysToDeduct <= 0) {
            throw new IllegalArgumentException("Days to deduct must be greater than zero");
        }

        if (getRemainingBalance() < daysToDeduct) {
            throw new InsufficientLeaveBalanceException(
                    "Cannot deduct " + daysToDeduct + " days. Only " + getRemainingBalance() + " days remaining."
            );
        }

        this.usedAllowance += daysToDeduct;
    }

    public void restoreLeave(double daysToRestore) {
        if (daysToRestore <= 0) {
            throw new IllegalArgumentException("Days to restore must be greater than zero");
        }

        this.usedAllowance -= daysToRestore;

        if (this.usedAllowance < 0) {
            this.usedAllowance = 0;
        }
    }

    // A helper method so controllers can easily check the balance
    public double getRemainingBalance() {
        return this.totalAllowance - this.usedAllowance;
    }
}
