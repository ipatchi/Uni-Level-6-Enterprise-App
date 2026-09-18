package staffs.leaverequestapp.staff;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import staffs.leaverequestapp.staff.ui.CreateStaffMemberCommand;
import staffs.leaverequestapp.staff.application.dto.StaffDTO;
import staffs.leaverequestapp.staff.domain.EmploymentStatus;
import staffs.leaverequestapp.staff.domain.EmploymentType;
import staffs.leaverequestapp.staff.ui.StaffMemberController;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StaffMemberController.class)
@DisplayName("Staff REST Controller Tests")
class StaffControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StaffContextFacade facade;

    @Test
    @DisplayName("POST /staff returns 201 Created and the new UUID")
    void createStaffEndpoint() throws Exception {
        UUID expectedId = UUID.randomUUID();
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
                EmploymentStatus.ACTIVE
        );

        when(facade.createStaffMember(any(CreateStaffMemberCommand.class))).thenReturn(expectedId);

        mockMvc.perform(post("/staff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(expectedId.toString()));

        verify(facade).createStaffMember(any(CreateStaffMemberCommand.class));
    }

    @Test
    @DisplayName("GET /staff/{staffId} returns 200 OK and serialized JSON")
    void getStaffEndpoint() throws Exception {
        UUID staffId = UUID.randomUUID();

        StaffDTO.OrganisationDTO orgDto = new StaffDTO.OrganisationDTO(LocalDate.now(), "Testing", null);
        StaffDTO.PlacementDTO placementDto = new StaffDTO.PlacementDTO("Tester", LocalDate.now(), "L6", EmploymentType.FULL_TIME);
        StaffDTO mockDto = new StaffDTO(staffId, "Test", "User", "TestUser@email.com", orgDto, placementDto, EmploymentStatus.ACTIVE);

        when(facade.getStaffById(staffId)).thenReturn(mockDto);

        mockMvc.perform(get("/staff/{staffId}", staffId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(staffId.toString()))
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.email").value("TestUser@email.com"))
                .andExpect(jsonPath("$.organisation.department").value("Testing"));

        verify(facade).getStaffById(staffId);
    }
}
