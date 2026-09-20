package staffs.leaverequestapp.leave.ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import staffs.leaverequestapp.leave.application.dto.LeaveRequestDTO;
import staffs.leaverequestapp.leave.domain.LeaveStatus;
import staffs.leaverequestapp.leave.LeaveContextFacade;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LeaveRequestController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Leave request controller unit tests")
class LeaveRequestControllerTests {

    private static final UUID VALID_STAFF_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final String VALID_REQUEST_ID = "REQ-123";
    private static final String STAFF_IDENTITY_ID = "firebase-staff";
    private static final String MANAGER_IDENTITY_ID = "firebase-manager";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LeaveContextFacade facade;

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
    @DisplayName("You can get your leave requests using the JWT identity")
    void getLeaveRequestsByStaffId() throws Exception {
        when(facade.findMyLeaveRequests(STAFF_IDENTITY_ID)).thenReturn(List.of(mockLeaveRequestDTO));

        mockMvc.perform(get("/leave-requests/me")
                        .principal(() -> STAFF_IDENTITY_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(VALID_REQUEST_ID))
                .andExpect(jsonPath("$[0].totalDays").value(2.0));

        verify(facade).findMyLeaveRequests(STAFF_IDENTITY_ID);
    }

    @Test
    @DisplayName("You can get your team's outstanding leave requests using the JWT identity")
    void getLeaveRequestsByManagerId() throws Exception {
        when(facade.findTeamLeaveOutstandingRequests(MANAGER_IDENTITY_ID, null, null))
                .thenReturn(List.of(mockLeaveRequestDTO));

        mockMvc.perform(get("/leave-requests/team")
                        .principal(() -> MANAGER_IDENTITY_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(VALID_REQUEST_ID));

        verify(facade).findTeamLeaveOutstandingRequests(MANAGER_IDENTITY_ID, null, null);
    }

    @Test
    @DisplayName("You can create a new leave request")
    void createLeaveRequest() throws Exception {
        String jsonPayload = """
                {
                    "startDate": "2026-10-01",
                    "endDate": "2026-10-05",
                    "reason": "Vacation"
                }
                """;

        mockMvc.perform(post("/leave-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                .principal(() -> STAFF_IDENTITY_ID)
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
                .principal(() -> STAFF_IDENTITY_ID)
                .content(malformedJson))
                .andExpect(status().isBadRequest());

        verify(facade, never()).makeLeaveRequest(any());
    }

    @Test
    @DisplayName("You can approve a leave request")
    void approveLeaveRequest() throws Exception {
        mockMvc.perform(patch("/leave-requests/{request_id}/approve", VALID_REQUEST_ID)
                        .with(authentication(new UsernamePasswordAuthenticationToken(
                                MANAGER_IDENTITY_ID, null,
                                List.of(new SimpleGrantedAuthority("ROLE_MANAGER")))))
                        .principal(new UsernamePasswordAuthenticationToken(
                                MANAGER_IDENTITY_ID, null,
                                List.of(new SimpleGrantedAuthority("ROLE_MANAGER")))))
                .andExpect(status().isOk());

        verify(facade).approveLeaveRequest(any(ApproveLeaveRequestCommand.class));
    }

    @Test
    @DisplayName("You can reject a leave request")
    void rejectLeaveRequest() throws Exception {
        mockMvc.perform(patch("/leave-requests/{request_id}/reject", VALID_REQUEST_ID)
                        .with(authentication(new UsernamePasswordAuthenticationToken(
                                MANAGER_IDENTITY_ID, null,
                                List.of(new SimpleGrantedAuthority("ROLE_MANAGER")))))
                        .principal(new UsernamePasswordAuthenticationToken(
                                MANAGER_IDENTITY_ID, null,
                                List.of(new SimpleGrantedAuthority("ROLE_MANAGER")))))
                .andExpect(status().isOk());

        verify(facade).rejectLeaveRequest(any(RejectLeaveRequestCommand.class));
    }

    @Test
    @DisplayName("An admin can approve a leave request")
    void adminCanApproveLeaveRequest() throws Exception {
        mockMvc.perform(patch("/leave-requests/{request_id}/approve", VALID_REQUEST_ID)
                        .with(authentication(new UsernamePasswordAuthenticationToken(
                                "firebase-admin", null,
                                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))))
                        .principal(new UsernamePasswordAuthenticationToken(
                                "firebase-admin", null,
                                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))))
                .andExpect(status().isOk());

        var commandCaptor = org.mockito.ArgumentCaptor.forClass(ApproveLeaveRequestCommand.class);
        verify(facade).approveLeaveRequest(commandCaptor.capture());
        org.junit.jupiter.api.Assertions.assertTrue(commandCaptor.getValue().adminOverride());
    }
}