package staffs.leaverequestapp.leave;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import staffs.leaverequestapp.leave.application.LeaveAllowanceQueryHandler;
import staffs.leaverequestapp.leave.application.LeaveRequestApplicationService;
import staffs.leaverequestapp.leave.application.LeaveRequestQueryHandler;
import staffs.leaverequestapp.leave.application.dto.LeaveAllowanceDTO;
import staffs.leaverequestapp.leave.application.dto.LeaveRequestDTO;
import staffs.leaverequestapp.leave.ui.ApproveLeaveRequestCommand;
import staffs.leaverequestapp.leave.ui.RejectLeaveRequestCommand;
import staffs.leaverequestapp.leave.ui.SubmitLeaveRequestCommand;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ContextFacade.class})
@DisplayName("Context Facade Security and Unit Tests")
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
class ContextFacadeTests {

    private final UUID VALID_MANAGER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private final UUID VALID_STAFF_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private final String VALID_REQUEST_ID = "REQ-123";
    private final int VALID_YEAR = 2026;

    @Autowired
    private ContextFacade facade;

    @MockitoBean
    private LeaveAllowanceQueryHandler leaveAllowanceQueryHandler;

    @MockitoBean
    private LeaveRequestQueryHandler leaveRequestQueryHandler;

    @MockitoBean
    private LeaveRequestApplicationService leaveRequestApplicationService;

    private LeaveAllowanceDTO mockAllowanceDTO;
    private LeaveRequestDTO mockRequestDTO;
    private SubmitLeaveRequestCommand mockSubmitCommand;
    private ApproveLeaveRequestCommand mockApproveLeaveRequestCommand;
    private RejectLeaveRequestCommand mockRejectLeaveRequestCommand;

    @BeforeEach
    void setUp() {
        mockAllowanceDTO = mock(LeaveAllowanceDTO.class);
        mockRequestDTO = mock(LeaveRequestDTO.class);
        mockSubmitCommand = mock(SubmitLeaveRequestCommand.class);
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("MANAGER role can access get team leave requests")
    void managerCanGetTeamLeaveRequests() {
        // Arrange: Stub the record method so our stream mapping in the facade extracts the ID correctly
        when(mockAllowanceDTO.staffId()).thenReturn(VALID_STAFF_ID);
        when(leaveAllowanceQueryHandler.findLeaveAllowanceByManagerId(VALID_MANAGER_ID))
                .thenReturn(List.of(mockAllowanceDTO));
        when(leaveRequestQueryHandler.findLeaveRequestsByStaffIds(List.of(VALID_STAFF_ID)))
                .thenReturn(List.of(mockRequestDTO));

        // Act
        List<LeaveRequestDTO> result = facade.findLeaveRequestsByManagerId(VALID_MANAGER_ID);

        // Assert
        assertThat(result).containsExactly(mockRequestDTO);
        verify(leaveAllowanceQueryHandler).findLeaveAllowanceByManagerId(VALID_MANAGER_ID);
        verify(leaveRequestQueryHandler).findLeaveRequestsByStaffIds(List.of(VALID_STAFF_ID));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("USER role is denied access to get team leave requests")
    void userCannotGetTeamLeaveRequests() {
        assertThatThrownBy(() -> facade.findLeaveRequestsByManagerId(VALID_MANAGER_ID))
                .isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(leaveAllowanceQueryHandler);
        verifyNoInteractions(leaveRequestQueryHandler);
    }

    @Test
    @WithMockUser(roles = "STAFF")
    @DisplayName("STAFF role can submit a new leave request")
    void userCanSubmitLeaveRequest() {
        when(leaveRequestApplicationService.submitLeaveRequest(mockSubmitCommand)).thenReturn(VALID_REQUEST_ID);

        assertThatNoException().isThrownBy(() -> facade.makeLeaveRequest(mockSubmitCommand));
        verify(leaveRequestApplicationService).submitLeaveRequest(mockSubmitCommand);
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("MANAGER role can approve a leave request")
    void managerCanApproveRequest() {
        assertThatNoException().isThrownBy(() -> facade.approveLeaveRequest(mockApproveLeaveRequestCommand));
        verify(leaveRequestApplicationService).approveLeaveRequest(mockApproveLeaveRequestCommand);
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("USER role is denied access to approve a leave request")
    void userCannotApproveRequest() {
        assertThatThrownBy(() -> facade.approveLeaveRequest(mockApproveLeaveRequestCommand))
                .isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(leaveRequestApplicationService);
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("MANAGER role can reject a leave request")
    void managerCanRejectRequest() {
        // Act & Assert
        assertThatNoException().isThrownBy(() -> facade.rejectLeaveRequest(mockRejectLeaveRequestCommand));
        verify(leaveRequestApplicationService).rejectLeaveRequest(mockRejectLeaveRequestCommand);
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("USER role is denied access to reject a leave request")
    void userCannotRejectRequest() {
        assertThatThrownBy(() -> facade.rejectLeaveRequest(mockRejectLeaveRequestCommand))
                .isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(leaveRequestApplicationService);
    }
}