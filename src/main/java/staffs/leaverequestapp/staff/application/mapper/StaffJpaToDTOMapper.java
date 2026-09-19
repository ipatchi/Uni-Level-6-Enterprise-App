package staffs.leaverequestapp.staff.application.mapper;

import staffs.leaverequestapp.staff.application.dto.StaffDTO;
import staffs.leaverequestapp.staff.persistance.entities.StaffJpa;

import java.util.UUID;

public class StaffJpaToDTOMapper {
    public static StaffDTO toStaffDTO(StaffJpa staffJpa) {
        if (staffJpa == null) {
            throw new NullPointerException("Staff JPA entity cannot be null");
        }

        StaffDTO.OrganisationDTO orgDTO = new StaffDTO.OrganisationDTO(
                staffJpa.getOrganisation().getHireDate(),
                staffJpa.getOrganisation().getDepartment(),
                staffJpa.getOrganisation().getLineManagerId()
        );

        StaffDTO.PlacementDTO placementDTO = new StaffDTO.PlacementDTO(
                staffJpa.getPlacement().getCurrentRole(),
                staffJpa.getPlacement().getRoleStartDate(),
                staffJpa.getPlacement().getJobLevel(),
                staffJpa.getPlacement().getEmploymentType()
        );

        return new StaffDTO(
                staffJpa.getStaffId(),
                staffJpa.getIdentity().firstName(),
                staffJpa.getIdentity().surname(),
                staffJpa.getEmail(),
                orgDTO,
                placementDTO,
                staffJpa.getEmploymentStatus(),
                staffJpa.getIdentityId()
        );
    }
}
