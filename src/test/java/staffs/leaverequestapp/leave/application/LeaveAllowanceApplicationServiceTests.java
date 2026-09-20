package staffs.leaverequestapp.leave.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import staffs.leaverequestapp.common.domain.FullName;
import staffs.leaverequestapp.leave.persistance.entities.LeaveAllowanceJpa;
import staffs.leaverequestapp.leave.persistance.repositories.LeaveAllowanceRepository;
import staffs.leaverequestapp.leave.persistance.repositories.LeaveRequestRepository;
import staffs.leaverequestapp.leave.domain.exceptions.LeaveAllowanceNotFoundException;
import staffs.leaverequestapp.leave.ui.AddLeaveAllowanceCommand;
import staffs.leaverequestapp.leave.ui.AmmendLeaveAllowanceCommand;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeaveAllowanceApplicationServiceTests {

    private static final UUID STAFF_ID =
            UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID MANAGER_ID =
            UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final String IDENTITY_ID = "firebase-staff";

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @Mock
    private LeaveAllowanceRepository leaveAllowanceRepository;

    @InjectMocks
    private LeaveAllowanceApplicationService service;

    @Test
    void addAllowancePersistsStaffAndIdentityReferences() {
        AddLeaveAllowanceCommand command = new AddLeaveAllowanceCommand(
                STAFF_ID,
                MANAGER_ID,
                2026,
                25.0,
                new FullName("Test", "User"),
                IDENTITY_ID
        );

        String result = service.addLeaveAllowance(command);

        ArgumentCaptor<LeaveAllowanceJpa> captor =
                ArgumentCaptor.forClass(LeaveAllowanceJpa.class);
        verify(leaveAllowanceRepository).save(captor.capture());

        LeaveAllowanceJpa saved = captor.getValue();
        assertThat(result).isEqualTo(saved.getId());
        assertThat(saved.getStaffId()).isEqualTo(STAFF_ID);
        assertThat(saved.getManagerId()).isEqualTo(MANAGER_ID);
        assertThat(saved.getIdentityId()).isEqualTo(IDENTITY_ID);
        assertThat(saved.getTotalAllowance()).isEqualTo(25.0);
    }

    @Test
    void amendAllowanceDoesNotRequireTheAdminToHaveOwnAllowance() {
        when(leaveAllowanceRepository.findByStaffId(STAFF_ID))
                .thenReturn(Optional.of(allowance(10.0)));

        service.ammendLeaveAllowance(
                new AmmendLeaveAllowanceCommand("firebase-admin", STAFF_ID, 30.0));

        verify(leaveAllowanceRepository).findByStaffId(STAFF_ID);
        verify(leaveAllowanceRepository).save(any(LeaveAllowanceJpa.class));
    }

    @Test
    void amendAllowanceUpdatesTheRequestedStaffAllowance() {
        LeaveAllowanceJpa allowance = allowance(10.0);

        when(leaveAllowanceRepository.findByStaffId(STAFF_ID))
                .thenReturn(Optional.of(allowance));
        when(leaveAllowanceRepository.save(any(LeaveAllowanceJpa.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        LeaveAllowanceJpa saved = service.ammendLeaveAllowance(
                new AmmendLeaveAllowanceCommand(IDENTITY_ID, STAFF_ID, 30.0));

        assertThat(saved.getTotalAllowance()).isEqualTo(30.0);
        assertThat(saved.getUsedAllowance()).isEqualTo(10.0);
    }

    private LeaveAllowanceJpa allowance(double usedAllowance) {
        LeaveAllowanceJpa allowance = new LeaveAllowanceJpa();
        allowance.setId(UUID.randomUUID().toString());
        allowance.setStaffId(STAFF_ID);
        allowance.setManagerId(MANAGER_ID);
        allowance.setYear(2026);
        allowance.setTotalAllowance(25.0);
        allowance.setUsedAllowance(usedAllowance);
        allowance.setFullName(new FullName("Test", "User"));
        allowance.setIdentityId(IDENTITY_ID);
        return allowance;
    }
}
