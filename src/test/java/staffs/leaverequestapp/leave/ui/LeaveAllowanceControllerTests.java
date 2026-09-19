package staffs.leaverequestapp.leave.ui;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import staffs.leaverequestapp.leave.application.dto.LeaveAllowanceDTO;
import staffs.leaverequestapp.leave.LeaveContextFacade;
import staffs.leaverequestapp.leave.ui.LeaveAllowanceController;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LeaveAllowanceController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Leave allowance controller unit tests")
class LeaveAllowanceControllerTests {

    private static final UUID VALID_STAFF_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID VALID_MANAGER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LeaveContextFacade facade;

    @Test
    @DisplayName("You can get the leave balance for a staff member")
    void getLeaveBalanceForStaff() throws Exception {
        UUID expectedId = UUID.randomUUID();
        LeaveAllowanceDTO mockDto = new LeaveAllowanceDTO(
                expectedId, VALID_STAFF_ID, "test", "user", VALID_MANAGER_ID, 2026, 25.0, 5.0,
                "firebase-staff"
        );

        when(facade.findMyLeaveAllowance("firebase-staff")).thenReturn(mockDto);

        mockMvc.perform(get("/leave-allowance/me")
                        .principal(() -> "firebase-staff"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedId.toString()))
                .andExpect(jsonPath("$.totalAllowance").value(25.0))
                .andExpect(jsonPath("$.remainingAllowance").value(20.0));

        verify(facade).findMyLeaveAllowance("firebase-staff");
    }

    @Test
    @DisplayName("You can get a list of leave balances by manager id")
    void getLeaveBalancesForManager() throws Exception {
        UUID expectedId = UUID.randomUUID();
        LeaveAllowanceDTO mockDto = new LeaveAllowanceDTO(
                expectedId, VALID_STAFF_ID, "test", "user", VALID_MANAGER_ID, 2026, 25.0, 5.0,
                "firebase-staff"
        );

        when(facade.findMyTeamLeaveAllowances("firebase-manager")).thenReturn(List.of(mockDto));

        mockMvc.perform(get("/leave-allowance/team")
                        .principal(() -> "firebase-manager"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(expectedId.toString()))
                .andExpect(jsonPath("$[0].totalAllowance").value(25.0));

        verify(facade).findMyTeamLeaveAllowances("firebase-manager");
    }

    @Test
    @DisplayName("Allowance edits use the authenticated JWT identity")
    void editAllowanceUsesAuthenticatedIdentity() throws Exception {
        mockMvc.perform(patch("/leave-allowance/edit")
                        .principal(() -> "firebase-admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "staffId": "11111111-1111-1111-1111-111111111111",
                                  "totalAllowance": 30.0
                                }
                                """))
                .andExpect(status().isOk());

        org.mockito.ArgumentCaptor<AmmendLeaveAllowanceCommand> captor =
                org.mockito.ArgumentCaptor.forClass(AmmendLeaveAllowanceCommand.class);
        verify(facade).editLeaveAllowance(captor.capture());
        org.assertj.core.api.Assertions.assertThat(captor.getValue().identityId())
                .isEqualTo("firebase-admin");
        org.assertj.core.api.Assertions.assertThat(captor.getValue().staffId())
                .isEqualTo(VALID_STAFF_ID);
    }
}