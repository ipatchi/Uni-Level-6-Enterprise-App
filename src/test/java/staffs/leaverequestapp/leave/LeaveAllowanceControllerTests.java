package staffs.leaverequestapp.leave;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import staffs.leaverequestapp.leave.application.dto.LeaveAllowanceDTO;
import staffs.leaverequestapp.leave.ui.LeaveAllowanceController;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
                expectedId, VALID_STAFF_ID, "test", "user", VALID_MANAGER_ID, 2026, 25.0, 5.0
        );

        // Reverted to returning a single DTO
        when(facade.findLeaveAllowanceByUserId(VALID_STAFF_ID)).thenReturn(mockDto);

        mockMvc.perform(get("/leave-allowance/{staff_id}", VALID_STAFF_ID))
                .andExpect(status().isOk())
                // Reverted back to the root JSON object
                .andExpect(jsonPath("$.id").value(expectedId.toString()))
                .andExpect(jsonPath("$.totalAllowance").value(25.0))
                .andExpect(jsonPath("$.remainingAllowance").value(20.0));

        verify(facade).findLeaveAllowanceByUserId(VALID_STAFF_ID);
    }

    @Test
    @DisplayName("You cannot get the leave balance with a malformed staff id")
    void getLeaveBalanceForStaffWithMalformedId() throws Exception {
        mockMvc.perform(get("/leave-allowance/{staff_id}", "invalid-uuid"))
                .andExpect(status().isBadRequest());

        verify(facade, never()).findLeaveAllowanceByUserId(any());
    }

    @Test
    @DisplayName("You can get a list of leave balances by manager id")
    void getLeaveBalancesForManager() throws Exception {
        UUID expectedId = UUID.randomUUID();
        LeaveAllowanceDTO mockDto = new LeaveAllowanceDTO(
                expectedId, VALID_STAFF_ID, "test", "user", VALID_MANAGER_ID, 2026, 25.0, 5.0
        );

        when(facade.findLeaveAllowanceByManagerId(VALID_MANAGER_ID)).thenReturn(List.of(mockDto));

        mockMvc.perform(get("/leave-allowance/manager/{manager_id}", VALID_MANAGER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(expectedId.toString()))
                .andExpect(jsonPath("$[0].totalAllowance").value(25.0));

        verify(facade).findLeaveAllowanceByManagerId(VALID_MANAGER_ID);
    }

    @Test
    @DisplayName("You cannot get a manager's team leave balances with a malformed manager id")
    void getLeaveBalancesForManagerWithMalformedId() throws Exception {
        mockMvc.perform(get("/leave-allowance/manager/{manager_id}", "invalid-uuid"))
                .andExpect(status().isBadRequest());

        verify(facade, never()).findLeaveAllowanceByManagerId(any());
    }
}