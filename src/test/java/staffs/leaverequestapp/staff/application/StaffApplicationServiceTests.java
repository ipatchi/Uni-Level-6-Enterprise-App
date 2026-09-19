package staffs.leaverequestapp.staff.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import staffs.leaverequestapp.common.events.infra.DomainEventManager;
import staffs.leaverequestapp.staff.application.StaffApplicationService;
import staffs.leaverequestapp.staff.ui.CreateStaffMemberCommand;
import staffs.leaverequestapp.staff.domain.EmploymentStatus;
import staffs.leaverequestapp.staff.domain.EmploymentType;
import staffs.leaverequestapp.staff.persistance.entities.StaffJpa;
import staffs.leaverequestapp.staff.persistance.repositories.StaffRepository;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Staff Application Service Tests")
class StaffApplicationServiceTests {

    @Mock
    private StaffRepository staffRepository;

    @Mock
    private DomainEventManager domainEventManager;

    @InjectMocks
    private StaffApplicationService service;

    @Test
    @DisplayName("Successfully creates a staff member, maps to JPA, and saves to repository")
    void createStaffMember() {
        CreateStaffMemberCommand command = new CreateStaffMemberCommand(
                "Test",
                "User",
                "TestUser@email.com",
                LocalDate.now(),
                "Testing",
                null,
                "Tester",
                LocalDate.now(),
                "L2",
                EmploymentType.FULL_TIME,
                EmploymentStatus.ACTIVE,
                "firebase-identity"
        );

        UUID generatedId = service.createStaffMember(command);

        ArgumentCaptor<StaffJpa> jpaCaptor = ArgumentCaptor.forClass(StaffJpa.class);
        verify(staffRepository).save(jpaCaptor.capture());

        StaffJpa savedEntity = jpaCaptor.getValue();

        assertNotNull(generatedId);
        assertEquals(generatedId, savedEntity.getStaffId());
        assertEquals("firebase-identity", savedEntity.getIdentityId());

        assertEquals("Test", savedEntity.getIdentity().firstName());
        assertEquals("User", savedEntity.getIdentity().surname());
        assertEquals("TestUser@email.com", savedEntity.getEmail());

        assertEquals(EmploymentStatus.ACTIVE, savedEntity.getEmploymentStatus());

        assertNotNull(savedEntity.getOrganisation());
        assertEquals("Testing", savedEntity.getOrganisation().getDepartment());

        assertNotNull(savedEntity.getPlacement());
        assertEquals("Tester", savedEntity.getPlacement().getCurrentRole());
    }
}
