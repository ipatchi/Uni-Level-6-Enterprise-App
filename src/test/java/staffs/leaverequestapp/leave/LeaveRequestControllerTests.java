package staffs.leaverequestapp.leave;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import staffs.leaverequestapp.leave.application.dto.LeaveRequestDTO;
import staffs.leaverequestapp.leave.domain.LeaveStatus;
import staffs.leaverequestapp.leave.ui.ApproveLeaveRequestCommand;
import staffs.leaverequestapp.leave.ui.LeaveRequestController;
import staffs.leaverequestapp.leave.ui.RejectLeaveRequestCommand;
import staffs.leaverequestapp.leave.ui.SubmitLeaveRequestCommand;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LeaveRequestController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Leave request controller unit tests")
class LeaveRequestControllerTests {

    private static final UUID VALID_STAFF_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID VALID_MANAGER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final String VALID_REQUEST_ID = "REQ-123";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContextFacade facade;

    private LeaveRequestDTO mockLeaveRequestDTO;

    @BeforeEach
    void setUp() {
        mockLeaveRequestDTO = new LeaveRequestDTO(
                VALID_REQUEST_ID,
                VALID_STAFF_ID,
                LocalDate.now(),
                LocalDate.now().plusDays(2),
                2.0,
                "Holiday",
                LeaveStatus.PENDING
        );
    }

    @Test
    @DisplayName("You can get leave requests by staff id")
    void getLeaveRequestsByStaffId() throws Exception {
        when(facade.findLeaveRequestsByStaffId(VALID_STAFF_ID)).thenReturn(List.of(mockLeaveRequestDTO));

        mockMvc.perform(get("/leave-requests/{staff_id}", VALID_STAFF_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(VALID_REQUEST_ID))
                .andExpect(jsonPath("$[0].totalDays").value(2.0));

        verify(facade).findLeaveRequestsByStaffId(VALID_STAFF_ID);
    }

    @Test
    @DisplayName("You cannot get leave requests with a malformed staff id")
    void getLeaveRequestsByStaffIdWithMalformedId() throws Exception {
        mockMvc.perform(get("/leave-requests/{staff_id}", "invalid-uuid"))
                .andExpect(status().isBadRequest());

        verify(facade, never()).findLeaveRequestsByStaffId(any());
    }

    @Test
    @DisplayName("You can get leave requests by manager id")
    void getLeaveRequestsByManagerId() throws Exception {
        when(facade.findLeaveRequestsByManagerId(VALID_MANAGER_ID)).thenReturn(List.of(mockLeaveRequestDTO));

        mockMvc.perform(get("/leave-requests/manager/{manager_id}", VALID_MANAGER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(VALID_REQUEST_ID));

        verify(facade).findLeaveRequestsByManagerId(VALID_MANAGER_ID);
    }

    @Test
    @DisplayName("You can create a new leave request")
    void createLeaveRequest() throws Exception {
        String jsonPayload = """
                {
                    "staffId": "11111111-1111-1111-1111-111111111111",
                    "startDate": "2026-10-01",
                    "endDate": "2026-10-05",
                    "reason": "Vacation"
                }
                """;

        mockMvc.perform(post("/leave-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated());

        verify(facade).makeLeaveRequest(any(SubmitLeaveRequestCommand.class));
    }

    @Test
    @DisplayName("You cannot create a leave request with malformed JSON data")
    void createLeaveRequestWithMalformedJson() throws Exception {
        String malformedJson = "{ \"staffId\": \"123\", \"startDate\": \"missing-quotes-and-braces }";

        mockMvc.perform(post("/leave-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());

        verify(facade, never()).makeLeaveRequest(any());
    }

    @Test
    @DisplayName("You can approve a leave request")
    void approveLeaveRequest() throws Exception {
        String jsonPayload = """
                {
                    "leaveRequestId": "REQ-123",
                    "managerId": "22222222-2222-2222-2222-222222222222"
                }
                """;

        mockMvc.perform(patch("/leave-requests/{request_id}/approve", VALID_REQUEST_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk());

        verify(facade).approveLeaveRequest(any(ApproveLeaveRequestCommand.class));
    }

    @Test
    @DisplayName("You can reject a leave request")
    void rejectLeaveRequest() throws Exception {
        String jsonPayload = """
                {
                    "leaveRequestId": "REQ-123",
                    "managerId": "22222222-2222-2222-2222-222222222222"
                }
                """;

        mockMvc.perform(patch("/leave-requests/{request_id}/reject", VALID_REQUEST_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk());

        verify(facade).rejectLeaveRequest(any(RejectLeaveRequestCommand.class));
    }
}