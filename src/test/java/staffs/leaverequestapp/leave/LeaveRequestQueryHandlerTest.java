package staffs.leaverequestapp.leave;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import staffs.leaverequestapp.leave.application.LeaveRequestQueryHandler;
import staffs.leaverequestapp.leave.application.dto.LeaveRequestDTO;
import staffs.leaverequestapp.leave.domain.LeaveStatus;
import staffs.leaverequestapp.leave.persistance.entities.LeaveRequestJpa;
import staffs.leaverequestapp.leave.persistance.repositories.LeaveRequestRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Leave Request Query Handler unit tests")
@ExtendWith(MockitoExtension.class)
class LeaveRequestQueryHandlerTest {

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @InjectMocks
    private LeaveRequestQueryHandler queryHandler;

    @Test
    @DisplayName("You can find leave requests for a list of staff IDs")
    void findLeaveRequestsByStaffIds() {
        // Arrange
        UUID staffId1 = UUID.randomUUID();
        UUID staffId2 = UUID.randomUUID();
        List<UUID> staffIds = List.of(staffId1, staffId2);

        LeaveRequestJpa requestJpa = new LeaveRequestJpa();
        requestJpa.setId("REQ-999");
        requestJpa.setStaffId(staffId1);
        requestJpa.setStartDate(LocalDate.now());
        requestJpa.setEndDate(LocalDate.now().plusDays(1));
        requestJpa.setTotalDays(2.0);
        requestJpa.setReason("Sick");
        requestJpa.setStatus(LeaveStatus.PENDING);

        when(leaveRequestRepository.findByStaffIdIn(staffIds)).thenReturn(List.of(requestJpa));

        List<LeaveRequestDTO> results = queryHandler.findLeaveRequestsByStaffIds(staffIds);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).id()).isEqualTo("REQ-999");
        verify(leaveRequestRepository).findByStaffIdIn(staffIds);
    }
}
