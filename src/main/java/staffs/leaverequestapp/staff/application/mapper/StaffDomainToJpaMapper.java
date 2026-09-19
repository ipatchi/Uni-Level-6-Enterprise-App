package staffs.leaverequestapp.staff.application.mapper;

import staffs.leaverequestapp.staff.domain.Organisation;
import staffs.leaverequestapp.staff.domain.Placement;
import staffs.leaverequestapp.staff.domain.StaffMember;
import staffs.leaverequestapp.staff.persistance.entities.OrganisationJpa;
import staffs.leaverequestapp.staff.persistance.entities.PlacementJpa;
import staffs.leaverequestapp.staff.persistance.entities.StaffJpa;

public class StaffDomainToJpaMapper {

    private StaffDomainToJpaMapper() {}

    public static StaffJpa toJpa(StaffMember staffMember) {
        StaffJpa staffJpa = new StaffJpa();
        staffJpa.setStaffId(staffMember.getStaffId());
        staffJpa.setIdentity(staffMember.getIdentity());
        staffJpa.setEmail(staffMember.getEmail());
        staffJpa.setEmploymentStatus(staffMember.getEmploymentStatus());
        staffJpa.setIdentityId(staffMember.getIdentityId());

        OrganisationJpa orgJpa = new OrganisationJpa();
        orgJpa.setHireDate(staffMember.getOrganisation().hireDate());
        orgJpa.setDepartment(staffMember.getOrganisation().department());
        orgJpa.setLineManagerId(staffMember.getOrganisation().lineManagerId());
        staffJpa.setOrganisation(orgJpa);

        PlacementJpa placementJpa = new PlacementJpa();
        placementJpa.setCurrentRole(staffMember.getPlacement().currentRole());
        placementJpa.setRoleStartDate(staffMember.getPlacement().roleStartDate());
        placementJpa.setJobLevel(staffMember.getPlacement().jobLevel());
        placementJpa.setEmploymentType(staffMember.getPlacement().employmentType());
        staffJpa.setPlacement(placementJpa);

        return staffJpa;
    }
}
