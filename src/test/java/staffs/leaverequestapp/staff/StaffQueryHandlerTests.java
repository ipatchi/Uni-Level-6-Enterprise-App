package staffs.leaverequestapp.staff;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import staffs.leaverequestapp.common.domain.FullName;
import staffs.leaverequestapp.staff.application.StaffQueryHandler;
import staffs.leaverequestapp.staff.application.dto.StaffDTO;
import staffs.leaverequestapp.staff.domain.EmploymentStatus;
import staffs.leaverequestapp.staff.domain.EmploymentType;
import staffs.leaverequestapp.staff.domain.Organisation;
import staffs.leaverequestapp.staff.domain.Placement;
import staffs.leaverequestapp.staff.persistance.entities.OrganisationJpa;
import staffs.leaverequestapp.staff.persistance.entities.PlacementJpa;
import staffs.leaverequestapp.staff.persistance.entities.StaffJpa;
import staffs.leaverequestapp.staff.persistance.repositories.StaffRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Staff Query Handler Tests")
class StaffQueryHandlerTests {

    @Mock
    private StaffRepository staffRepository;

    @InjectMocks
    private StaffQueryHandler handler;

    @Test
    @DisplayName("Successfully fetches a staff member by ID and maps to a DTO")
    void getStaffByIdSuccess() {
        UUID targetId = UUID.randomUUID();
        StaffJpa mockJpa = new StaffJpa();
        mockJpa.setStaffId(targetId.toString());
        mockJpa.setIdentity(new FullName("Test", "User"));
        mockJpa.setEmail("TestUser@email.com");
        mockJpa.setEmploymentStatus(EmploymentStatus.ACTIVE);

        OrganisationJpa mockOrgJpa = new OrganisationJpa();
        mockOrgJpa.setDepartment("Testing");
        mockOrgJpa.setHireDate(LocalDate.now());
        mockJpa.setOrganisation(mockOrgJpa);

        PlacementJpa mockPlacementJpa = new PlacementJpa();
        mockPlacementJpa.setCurrentRole("Tester");
        mockPlacementJpa.setRoleStartDate(LocalDate.now());
        mockPlacementJpa.setJobLevel("L4");
        mockPlacementJpa.setEmploymentType(EmploymentType.FULL_TIME);
        mockJpa.setPlacement(mockPlacementJpa);

        when(staffRepository.findById(targetId.toString())).thenReturn(Optional.of(mockJpa));

        StaffDTO resultDto = handler.getStaffById(targetId);

        assertEquals(targetId, resultDto.id());
        assertEquals("Test", resultDto.firstName());
        assertEquals("TestUser@email.com", resultDto.email());
        assertEquals("Testing", resultDto.organisation().department());

        verify(staffRepository).findById(targetId.toString());
    }

    @Test
    @DisplayName("Throws an exception when requesting a staff ID that does not exist")
    void getStaffByIdNotFound() {
        UUID missingId = UUID.randomUUID();
        when(staffRepository.findById(missingId.toString())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                handler.getStaffById(missingId)
        );

        assertEquals("Staff member not found with ID: " + missingId, exception.getMessage());
        verify(staffRepository).findById(missingId.toString());
    }
}
