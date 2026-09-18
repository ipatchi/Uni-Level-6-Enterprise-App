package staffs.leaverequestapp.staff;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import staffs.leaverequestapp.staff.application.dto.StaffDTO;
import staffs.leaverequestapp.staff.domain.EmploymentStatus;
import staffs.leaverequestapp.staff.domain.EmploymentType;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Staff DTO Validation Tests")
public class StaffDTOTests {
    private final UUID validId = UUID.randomUUID();
    private final StaffDTO.OrganisationDTO validOrg = new StaffDTO.OrganisationDTO(LocalDate.now(), "Engineering", null);
    private final StaffDTO.PlacementDTO validPlacement = new StaffDTO.PlacementDTO("Developer", LocalDate.now(), "L3", EmploymentType.FULL_TIME);

    @Test
    @DisplayName("Successfully creates a fully populated DTO with nested records")
    void validDTO() {
        StaffDTO dto = new StaffDTO(
                validId, "Test", "User", "testuser@email.com", validOrg, validPlacement, EmploymentStatus.ACTIVE
        );

        assertNotNull(dto);
        assertEquals(validId, dto.id());
        assertEquals("Test", dto.firstName());
        assertEquals("User", dto.surname());
        assertEquals("testuser@email.com", dto.email());
        assertEquals(EmploymentStatus.ACTIVE, dto.status());

        assertNotNull(dto.organisation());
        assertEquals("Engineering", dto.organisation().department());

        assertNotNull(dto.placement());
        assertEquals("Developer", dto.placement().currentRole());
    }


    @Test
    @DisplayName("DTO creation fails if required identity fields are null")
    void nullValidation() {
        assertThrows(NullPointerException.class, () -> new StaffDTO(
                UUID.randomUUID(), null, "user", "testuser@email.com", null, null, EmploymentStatus.ACTIVE
        ), "First name cannot be null");
    }
}
