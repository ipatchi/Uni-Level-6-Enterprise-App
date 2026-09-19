package staffs.leaverequestapp.leave.application.mapper;

import staffs.leaverequestapp.common.domain.FullName;
import staffs.leaverequestapp.common.domain.Identity;
import staffs.leaverequestapp.leave.domain.LeaveAllowance;
import staffs.leaverequestapp.leave.persistance.entities.LeaveAllowanceJpa;

public class LeaveAllowanceJpaToDomainMapper {

    public static LeaveAllowance map(LeaveAllowanceJpa leaveAllowanceJpa) {
        if (leaveAllowanceJpa == null) return null;

        Identity<LeaveAllowance> id = Identity.of(leaveAllowanceJpa.getId());

        FullName fullName = null;
        if (leaveAllowanceJpa.getFullName() != null) {
            fullName = new FullName(
                    leaveAllowanceJpa.getFullName().firstName(),
                    leaveAllowanceJpa.getFullName().surname()
            );
        }

        return LeaveAllowance.leaveAllowanceOf(
                id,
                leaveAllowanceJpa.getStaffId(),
                fullName,
                leaveAllowanceJpa.getManagerId(),
                leaveAllowanceJpa.getYear(),
                leaveAllowanceJpa.getTotalAllowance(),
                leaveAllowanceJpa.getUsedAllowance(),
                leaveAllowanceJpa.getIdentityId()
        );
    }
}
