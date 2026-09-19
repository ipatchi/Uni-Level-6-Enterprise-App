package staffs.leaverequestapp.leave.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import staffs.leaverequestapp.common.domain.FullName;
import staffs.leaverequestapp.leave.application.LeaveRequestApplicationService;
import staffs.leaverequestapp.leave.domain.LeaveStatus;
import staffs.leaverequestapp.leave.persistance.entities.LeaveAllowanceJpa;
import staffs.leaverequestapp.leave.persistance.entities.LeaveRequestJpa;
import staffs.leaverequestapp.leave.persistance.repositories.LeaveAllowanceRepository;
import staffs.leaverequestapp.leave.persistance.repositories.LeaveRequestRepository;
import staffs.leaverequestapp.leave.ui.ApproveLeaveRequestCommand;
import staffs.leaverequestapp.leave.ui.CancelLeaveRequestCommand;
import staffs.leaverequestapp.leave.ui.RejectLeaveRequestCommand;
import staffs.leaverequestapp.leave.ui.SubmitLeaveRequestCommand;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Leave request application service unit tests")
@ExtendWith(MockitoExtension.class)
class LeaveRequestApplicationServiceTest {

    private static final UUID VALID_STAFF_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID VALID_MANAGER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final String STAFF_IDENTITY_ID = "firebase-staff-id";
    private static final String MANAGER_IDENTITY_ID = "firebase-manager-id";
    private static final String VALID_REQUEST_ID = "REQ-123";
    private static final LocalDate START_DATE = LocalDate.now().plusDays(7);
    private static final LocalDate END_DATE = LocalDate.now().plusDays(9);
    private static final String VALID_REASON = "Annual Vacation";
    private static final double TOTAL_ALLOWANCE = 25.0;

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @Mock
    private LeaveAllowanceRepository leaveAllowanceRepository;

    @InjectMocks
    private LeaveRequestApplicationService leaveRequestApplicationService;

    private LeaveAllowanceJpa mockAllowanceJpa;
    private LeaveRequestJpa mockRequestJpa;

    @BeforeEach
    void setUp() {
        // Setup default valid Allowance JPA entity
        mockAllowanceJpa = new LeaveAllowanceJpa();
        mockAllowanceJpa.setId(UUID.randomUUID().toString());
        mockAllowanceJpa.setStaffId(VALID_STAFF_ID);
        mockAllowanceJpa.setManagerId(VALID_MANAGER_ID);
        mockAllowanceJpa.setYear(START_DATE.getYear());
        mockAllowanceJpa.setTotalAllowance(TOTAL_ALLOWANCE);
        mockAllowanceJpa.setFullName(new FullName("Test","User"));
        mockAllowanceJpa.setUsedAllowance(5.0); // Pre-used some allowance
        mockAllowanceJpa.setIdentityId(STAFF_IDENTITY_ID);

        // Setup default valid Request JPA entity
        mockRequestJpa = new LeaveRequestJpa();
        mockRequestJpa.setId(VALID_REQUEST_ID);
        mockRequestJpa.setStaffId(VALID_STAFF_ID);
        mockRequestJpa.setStartDate(START_DATE);
        mockRequestJpa.setEndDate(END_DATE);
        mockRequestJpa.setReason(VALID_REASON);
        mockRequestJpa.setTotalDays(3.0);
        mockRequestJpa.setStatus(LeaveStatus.PENDING);
    }

    private LeaveAllowanceJpa managerAllowance() {
        LeaveAllowanceJpa managerAllowance = new LeaveAllowanceJpa();
        managerAllowance.setId(UUID.randomUUID().toString());
        managerAllowance.setStaffId(VALID_MANAGER_ID);
        managerAllowance.setManagerId(VALID_MANAGER_ID);
        managerAllowance.setYear(START_DATE.getYear());
        managerAllowance.setTotalAllowance(TOTAL_ALLOWANCE);
        managerAllowance.setFullName(new FullName("Manager", "User"));
        managerAllowance.setUsedAllowance(0.0);
        managerAllowance.setIdentityId(MANAGER_IDENTITY_ID);
        return managerAllowance;
    }

    @Test
    @DisplayName("You can submit a leave request when a valid allowance exists")
    void submitLeaveRequestWhenAllowanceExists() {
        SubmitLeaveRequestCommand command = new SubmitLeaveRequestCommand(
                STAFF_IDENTITY_ID, START_DATE, END_DATE, VALID_REASON
        );

        when(leaveAllowanceRepository.findByIdentityId(STAFF_IDENTITY_ID)).thenReturn(Optional.of(mockAllowanceJpa));
        when(leaveAllowanceRepository.findByStaffId(VALID_STAFF_ID)).thenReturn(Optional.of(mockAllowanceJpa));

        String resultId = leaveRequestApplicationService.submitLeaveRequest(command);

        assertThat(resultId).isNotNull();

        ArgumentCaptor<LeaveAllowanceJpa> allowanceCaptor = ArgumentCaptor.forClass(LeaveAllowanceJpa.class);
        verify(leaveAllowanceRepository).save(allowanceCaptor.capture());
        LeaveAllowanceJpa savedAllowance = allowanceCaptor.getValue();

        assertEquals(8.0, savedAllowance.getUsedAllowance());

        ArgumentCaptor<LeaveRequestJpa> requestCaptor = ArgumentCaptor.forClass(LeaveRequestJpa.class);
        verify(leaveRequestRepository).save(requestCaptor.capture());
        LeaveRequestJpa savedRequest = requestCaptor.getValue();

        assertAll(
                () -> assertEquals(VALID_STAFF_ID, savedRequest.getStaffId()),
                () -> assertEquals(START_DATE, savedRequest.getStartDate()),
                () -> assertEquals(END_DATE, savedRequest.getEndDate()),
                () -> assertEquals(LeaveStatus.PENDING, savedRequest.getStatus())
        );
    }

    @Test
    @DisplayName("You cannot submit a leave request when the allowance does not exist")
    void throwWhenSubmittingAndAllowanceDoesNotExist() {
        SubmitLeaveRequestCommand command = new SubmitLeaveRequestCommand(
                STAFF_IDENTITY_ID, START_DATE, END_DATE, VALID_REASON
        );

        when(leaveAllowanceRepository.findByIdentityId(STAFF_IDENTITY_ID))
                .thenReturn(Optional.of(mockAllowanceJpa));
        when(leaveAllowanceRepository.findByStaffId(VALID_STAFF_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> leaveRequestApplicationService.submitLeaveRequest(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No leave allowance found");

        verify(leaveAllowanceRepository, never()).save(any());
        verify(leaveRequestRepository, never()).save(any());
    }

    @Test
    @DisplayName("You can approve a leave request when the request exists")
    void approveLeaveRequestWhenRequestExists() {
        ApproveLeaveRequestCommand command = new ApproveLeaveRequestCommand(MANAGER_IDENTITY_ID, VALID_REQUEST_ID);

        when(leaveRequestRepository.findById(VALID_REQUEST_ID)).thenReturn(Optional.of(mockRequestJpa));
        when(leaveAllowanceRepository.findByIdentityId(MANAGER_IDENTITY_ID)).thenReturn(Optional.of(managerAllowance()));
        when(leaveAllowanceRepository.findByStaffId(VALID_STAFF_ID)).thenReturn(Optional.of(mockAllowanceJpa));

        leaveRequestApplicationService.approveLeaveRequest(command);

        ArgumentCaptor<LeaveRequestJpa> requestCaptor = ArgumentCaptor.forClass(LeaveRequestJpa.class);
        verify(leaveRequestRepository).save(requestCaptor.capture());
        LeaveRequestJpa savedRequest = requestCaptor.getValue();

        assertEquals(LeaveStatus.APPROVED, savedRequest.getStatus());
    }

    @Test
    @DisplayName("You cannot approve a leave request when the request does not exist")
    void throwWhenApprovingAndRequestDoesNotExist() {
        ApproveLeaveRequestCommand command = new ApproveLeaveRequestCommand(MANAGER_IDENTITY_ID, "INVALID-ID");

        when(leaveRequestRepository.findById("INVALID-ID")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> leaveRequestApplicationService.approveLeaveRequest(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Leave request not found");

        verify(leaveRequestRepository, never()).save(any());
    }

    @Test
    @DisplayName("You can reject a leave request and restore allowance when both exist")
    void rejectLeaveRequestWhenRequestAndAllowanceExist() {
        RejectLeaveRequestCommand command = new RejectLeaveRequestCommand(MANAGER_IDENTITY_ID, VALID_REQUEST_ID);

        when(leaveRequestRepository.findById(VALID_REQUEST_ID)).thenReturn(Optional.of(mockRequestJpa));
        when(leaveAllowanceRepository.findByIdentityId(MANAGER_IDENTITY_ID)).thenReturn(Optional.of(managerAllowance()));
        when(leaveAllowanceRepository.findByStaffId(VALID_STAFF_ID)).thenReturn(Optional.of(mockAllowanceJpa));

        leaveRequestApplicationService.rejectLeaveRequest(command);

        ArgumentCaptor<LeaveRequestJpa> requestCaptor = ArgumentCaptor.forClass(LeaveRequestJpa.class);
        verify(leaveRequestRepository).save(requestCaptor.capture());
        assertEquals(LeaveStatus.REJECTED, requestCaptor.getValue().getStatus());

        ArgumentCaptor<LeaveAllowanceJpa> allowanceCaptor = ArgumentCaptor.forClass(LeaveAllowanceJpa.class);
        verify(leaveAllowanceRepository).save(allowanceCaptor.capture());
        assertEquals(2.0, allowanceCaptor.getValue().getUsedAllowance());
    }

    @Test
    @DisplayName("You cannot reject a leave request when the request does not exist")
    void throwWhenRejectingAndRequestDoesNotExist() {
        RejectLeaveRequestCommand command = new RejectLeaveRequestCommand(MANAGER_IDENTITY_ID, "INVALID-ID");

        when(leaveRequestRepository.findById("INVALID-ID")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> leaveRequestApplicationService.rejectLeaveRequest(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Leave request not found");

        verify(leaveAllowanceRepository, never()).findByStaffId(any());
        verify(leaveRequestRepository, never()).save(any());
        verify(leaveAllowanceRepository, never()).save(any());
    }

    @Test
    @DisplayName("You cannot reject a leave request when the linked allowance is missing")
    void throwWhenRejectingAndAllowanceDoesNotExist() {
        RejectLeaveRequestCommand command = new RejectLeaveRequestCommand(MANAGER_IDENTITY_ID, VALID_REQUEST_ID);

        when(leaveRequestRepository.findById(VALID_REQUEST_ID)).thenReturn(Optional.of(mockRequestJpa));
        when(leaveAllowanceRepository.findByIdentityId(MANAGER_IDENTITY_ID)).thenReturn(Optional.of(managerAllowance()));
        when(leaveAllowanceRepository.findByStaffId(VALID_STAFF_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> leaveRequestApplicationService.rejectLeaveRequest(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No leave allowance found");

        verify(leaveRequestRepository, never()).save(any());
        verify(leaveAllowanceRepository, never()).save(any());
    }

    @Test
    @DisplayName("A manager with the wrong identity cannot approve a staff member's request")
    void rejectApprovalFromWrongManager() {
        String anotherManagerIdentity = "firebase-another-manager";
        LeaveAllowanceJpa anotherManagerAllowance = managerAllowance();
        anotherManagerAllowance.setStaffId(UUID.fromString("33333333-3333-3333-3333-333333333333"));

        when(leaveRequestRepository.findById(VALID_REQUEST_ID)).thenReturn(Optional.of(mockRequestJpa));
        when(leaveAllowanceRepository.findByIdentityId(anotherManagerIdentity))
                .thenReturn(Optional.of(anotherManagerAllowance));
        when(leaveAllowanceRepository.findByStaffId(VALID_STAFF_ID))
                .thenReturn(Optional.of(mockAllowanceJpa));

        assertThatThrownBy(() -> leaveRequestApplicationService.approveLeaveRequest(
                new ApproveLeaveRequestCommand(anotherManagerIdentity, VALID_REQUEST_ID)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not the staff member's manager");

        verify(leaveRequestRepository, never()).save(any());
    }

    @Test
    @DisplayName("A staff member can cancel their own request using their IAM identity")
    void cancelOwnLeaveRequest() {
        when(leaveRequestRepository.findById(VALID_REQUEST_ID)).thenReturn(Optional.of(mockRequestJpa));
        when(leaveAllowanceRepository.findByIdentityId(STAFF_IDENTITY_ID))
                .thenReturn(Optional.of(mockAllowanceJpa));
        when(leaveAllowanceRepository.findByStaffId(VALID_STAFF_ID))
                .thenReturn(Optional.of(mockAllowanceJpa));

        String result = leaveRequestApplicationService.cancelLeaveRequest(
                new CancelLeaveRequestCommand(STAFF_IDENTITY_ID, VALID_REQUEST_ID));

        assertThat(result).isEqualTo(VALID_REQUEST_ID);
        verify(leaveRequestRepository).save(any(LeaveRequestJpa.class));
        verify(leaveAllowanceRepository).save(any(LeaveAllowanceJpa.class));
    }

    @Test
    @DisplayName("A staff member cannot cancel another staff member's request")
    void cannotCancelAnotherStaffMembersRequest() {
        UUID otherStaffId = UUID.fromString("33333333-3333-3333-3333-333333333333");
        mockRequestJpa.setStaffId(otherStaffId);

        when(leaveRequestRepository.findById(VALID_REQUEST_ID)).thenReturn(Optional.of(mockRequestJpa));
        when(leaveAllowanceRepository.findByIdentityId(STAFF_IDENTITY_ID))
                .thenReturn(Optional.of(mockAllowanceJpa));

        assertThatThrownBy(() -> leaveRequestApplicationService.cancelLeaveRequest(
                new CancelLeaveRequestCommand(STAFF_IDENTITY_ID, VALID_REQUEST_ID)))
                .isInstanceOf(staffs.leaverequestapp.leave.domain.exceptions.LeaveRequestCannotBeCancelledByProxyException.class);

        verify(leaveRequestRepository, never()).save(any());
    }
}
