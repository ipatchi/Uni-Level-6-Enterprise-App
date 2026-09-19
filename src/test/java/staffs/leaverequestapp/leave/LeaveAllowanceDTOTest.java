package staffs.leaverequestapp.leave;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import staffs.leaverequestapp.leave.application.dto.LeaveAllowanceDTO;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Leave allowance DTO unit tests")
class LeaveAllowanceDTOTest {

    @Test
    @DisplayName("You can calculate the remaining allowance correctly")
    void calculatesRemainingAllowance() {
        LeaveAllowanceDTO dto = new LeaveAllowanceDTO(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Test",
                "Data",
                UUID.randomUUID(),
                2026,
                25.0,
                4.5,
                "firebase-identity"
        );

        assertThat(dto.remainingAllowance()).isEqualTo(20.5);
    }
}
