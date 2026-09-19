package staffs.leaverequestapp.staff.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import staffs.leaverequestapp.common.domain.FullName;
import staffs.leaverequestapp.common.domain.Identity;
import staffs.leaverequestapp.staff.application.dto.StaffDTO;
import staffs.leaverequestapp.staff.application.mapper.StaffDomainToJpaMapper;
import staffs.leaverequestapp.staff.application.mapper.StaffJpaToDTOMapper;
import staffs.leaverequestapp.staff.domain.*;
import staffs.leaverequestapp.staff.persistance.entities.OrganisationJpa;
import staffs.leaverequestapp.staff.persistance.entities.PlacementJpa;
import staffs.leaverequestapp.staff.persistance.entities.StaffJpa;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Staff Mappers Tests")
class StaffMapperTests {

    @Test
    @DisplayName("Successfully maps a pure Domain StaffMember to a JPA Entity")
    void domainToJpa() {
        Identity<StaffMember> validId = Identity.generateId();

        Organisation org = new Organisation(LocalDate.now(), "Testing", null);
        Placement placement = new Placement("Tester", LocalDate.now(), "L2", EmploymentType.FULL_TIME);
        StaffMember domainStaff = StaffMember.hire(
                validId,
                new FullName("Test", "User"),
                "TestUser@email.com",
                org,
                placement,
                null,
                "firebase-identity"
        );

        StaffJpa jpa = StaffDomainToJpaMapper.toJpa(domainStaff);

        assertNotNull(jpa.getStaffId());
        assertEquals("firebase-identity", jpa.getIdentityId());
        assertEquals("TestUser@email.com", jpa.getEmail());
        assertEquals("Test", jpa.getIdentity().firstName());
        assertEquals("User", jpa.getIdentity().surname());
        assertEquals("Testing", jpa.getOrganisation().getDepartment());
        assertEquals("Tester", jpa.getPlacement().getCurrentRole());
    }

    @Test
    @DisplayName("Successfully maps a JPA Entity to a Presentation DTO")
    void jpaToDto() {
        UUID staffId = UUID.randomUUID();

        StaffJpa staffJpa = new StaffJpa();

        staffJpa.setStaffId(staffId);
        staffJpa.setIdentity(new FullName("Test", "User"));
        staffJpa.setEmail("TestUser@email.com");
        staffJpa.setEmploymentStatus(EmploymentStatus.ACTIVE);

        OrganisationJpa orgJpa = new OrganisationJpa();
        orgJpa.setDepartment("Testing");
        orgJpa.setHireDate(LocalDate.now());
        staffJpa.setOrganisation(orgJpa);

        PlacementJpa placementJpa = new PlacementJpa();
        placementJpa.setCurrentRole("Tester");
        placementJpa.setRoleStartDate(LocalDate.now());
        placementJpa.setJobLevel("L2");
        placementJpa.setEmploymentType(EmploymentType.FULL_TIME);
        staffJpa.setPlacement(placementJpa);

        StaffDTO dto = StaffJpaToDTOMapper.toStaffDTO(staffJpa);

        assertEquals(staffId, dto.id());
        assertEquals("Test", dto.firstName());
        assertEquals("User", dto.surname());
        assertEquals("TestUser@email.com", dto.email());
        assertEquals("Testing", dto.organisation().department());
        assertEquals("Tester", dto.placement().currentRole());
    }
}
