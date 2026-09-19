package staffs.leaverequestapp.staff.application.mapper;

import staffs.leaverequestapp.common.domain.FullName;
import staffs.leaverequestapp.common.domain.Identity;
import staffs.leaverequestapp.staff.domain.Organisation;
import staffs.leaverequestapp.staff.domain.Placement;
import staffs.leaverequestapp.staff.domain.StaffMember;
import staffs.leaverequestapp.staff.persistance.entities.StaffJpa;

public class StaffMemberJpaToDomainMapper {

    public static StaffMember map(StaffJpa staffJpa) {
        if (staffJpa == null) {
            return null;
        }

        Identity<StaffMember> staffId = new Identity<>(staffJpa.getStaffId().toString());


        return StaffMember.restore(
                staffId,
                new FullName(staffJpa.getIdentity().firstName(), staffJpa.getIdentity().surname()),
                staffJpa.getEmail(),
                new Organisation(
                        staffJpa.getOrganisation().getHireDate(),
                        staffJpa.getOrganisation().getDepartment(),
                        staffJpa.getOrganisation().getLineManagerId()
                ),
                new Placement(
                        staffJpa.getPlacement().getCurrentRole(),
                        staffJpa.getPlacement().getRoleStartDate(),
                        staffJpa.getPlacement().getJobLevel(),
                        staffJpa.getPlacement().getEmploymentType()
                ),
                staffJpa.getOrganisation().getLineManagerId(),
                staffJpa.getEmploymentStatus(),
                staffJpa.getIdentityId()
        );
    }
}
