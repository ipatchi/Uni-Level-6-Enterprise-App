package staffs.leaverequestapp.leave;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import staffs.leaverequestapp.common.domain.FullName;
import staffs.leaverequestapp.leave.application.LeaveAllowanceQueryHandler;
import staffs.leaverequestapp.leave.application.dto.LeaveAllowanceDTO;
import staffs.leaverequestapp.leave.persistance.entities.LeaveAllowanceJpa;
import staffs.leaverequestapp.leave.persistance.repositories.LeaveAllowanceRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Leave allowance query handler unit tests")
@ExtendWith(MockitoExtension.class)
class LeaveAllowanceQueryHandlerTest {

    @Mock
    private LeaveAllowanceRepository leaveAllowanceRepository;

    @InjectMocks
    private LeaveAllowanceQueryHandler queryHandler;

    @Test
    @DisplayName("You can get a leave balance for a staff member")
    void getLeaveBalanceForStaff() {
        UUID expectedId = UUID.randomUUID();
        UUID staffId = UUID.randomUUID();
        UUID managerId = UUID.randomUUID();

        LeaveAllowanceJpa jpa = new LeaveAllowanceJpa();
        jpa.setId(expectedId.toString());
        jpa.setFullName(new FullName("test","user"));
        jpa.setStaffId(staffId);
        jpa.setManagerId(managerId);
        jpa.setTotalAllowance(25.0);
        jpa.setUsedAllowance(5.0);
        jpa.setYear(2026);

        when(leaveAllowanceRepository.findByStaffId(staffId)).thenReturn(Optional.of(jpa));

        LeaveAllowanceDTO result = queryHandler.findLeaveAllowanceByUserId(staffId);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(expectedId);
        assertThat(result.totalAllowance()).isEqualTo(25.0);
        verify(leaveAllowanceRepository).findByStaffId(staffId);
    }

    @Test
    @DisplayName("You cannot get a leave balance when the allowance does not exist")
    void throwWhenAllowanceDoesNotExist() {
        UUID staffId = UUID.randomUUID();

        when(leaveAllowanceRepository.findByStaffId(staffId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> queryHandler.findLeaveAllowanceByUserId(staffId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Allowance could not be found");
    }

    @Test
    @DisplayName("You can get a list of leave balances by manager id")
    void getLeaveBalancesByManagerId() {
        UUID expectedId = UUID.randomUUID();
        UUID managerId = UUID.randomUUID();

        LeaveAllowanceJpa jpa = new LeaveAllowanceJpa();
        jpa.setId(expectedId.toString());
        jpa.setFullName(new FullName("test","user"));
        jpa.setStaffId(UUID.randomUUID());
        jpa.setManagerId(managerId);
        jpa.setTotalAllowance(25.0);
        jpa.setUsedAllowance(5.0);
        jpa.setYear(2026);

        when(leaveAllowanceRepository.findByManagerId(managerId)).thenReturn(List.of(jpa));

        List<LeaveAllowanceDTO> results = queryHandler.findLeaveAllowanceByManagerId(managerId);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).id()).isEqualTo(expectedId);
        verify(leaveAllowanceRepository).findByManagerId(managerId);
    }

    @Test
    @DisplayName("You receive an empty list when querying a manager id with no staff")
    void getEmptyLeaveBalancesByManagerId() {
        UUID managerId = UUID.randomUUID();

        when(leaveAllowanceRepository.findByManagerId(managerId)).thenReturn(List.of());

        List<LeaveAllowanceDTO> results = queryHandler.findLeaveAllowanceByManagerId(managerId);

        assertThat(results).isEmpty();
        verify(leaveAllowanceRepository).findByManagerId(managerId);
    }
}