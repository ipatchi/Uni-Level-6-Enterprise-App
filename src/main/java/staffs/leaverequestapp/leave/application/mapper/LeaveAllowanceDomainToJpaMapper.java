package staffs.leaverequestapp.leave.application.mapper;

import staffs.leaverequestapp.common.domain.FullName;
import staffs.leaverequestapp.leave.domain.LeaveAllowance;
import staffs.leaverequestapp.leave.persistance.entities.LeaveAllowanceJpa;

public class LeaveAllowanceDomainToJpaMapper {
    public static LeaveAllowanceJpa map(LeaveAllowance allowance) {
        if (allowance == null) return null;
        LeaveAllowanceJpa leaveAllowanceJpa = new LeaveAllowanceJpa();

        leaveAllowanceJpa.setId(allowance.id().id());
        leaveAllowanceJpa.setStaffId(allowance.getStaffId());

        FullName fullName = new FullName(allowance.getFullName().firstName(),
                allowance.getFullName().surname());

        leaveAllowanceJpa.setFullName(fullName);
        leaveAllowanceJpa.setManagerId(allowance.getManagerId());
        leaveAllowanceJpa.setYear(allowance.getYear());
        leaveAllowanceJpa.setTotalAllowance(allowance.getTotalAllowance());
        leaveAllowanceJpa.setUsedAllowance(allowance.getUsedAllowance());

        return leaveAllowanceJpa;
    }
}
