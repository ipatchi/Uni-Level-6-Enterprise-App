package staffs.leaverequestapp.leave;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import staffs.leaverequestapp.leave.application.dto.LeaveRequestDTO;
import staffs.leaverequestapp.leave.domain.LeaveStatus;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Leave Request DTO unit tests")
class LeaveRequestDTOTest {

    private final UUID VALID_STAFF_ID = UUID.randomUUID();
    private final UUID VALID_REVIEWER_ID = UUID.randomUUID();
    private final LocalDate START_DATE = LocalDate.now();
    private final LocalDate END_DATE = LocalDate.now().plusDays(2);

    @Test
    @DisplayName("You can create a valid LeaveRequestDTO")
    void canCreateValidDTO() {
        LeaveRequestDTO dto = new LeaveRequestDTO(
                "REQ-1", VALID_STAFF_ID, START_DATE, END_DATE, 3.0, "Holiday", LeaveStatus.PENDING
        );

        assertThat(dto.id()).isEqualTo("REQ-1");
        assertThat(dto.totalDays()).isEqualTo(3.0);
    }

    @Test
    @DisplayName("You cannot create a LeaveRequestDTO with zero or negative total days")
    void cannotCreateWithInvalidTotalDays() {
        assertThatThrownBy(() -> new LeaveRequestDTO(
                "REQ-1", VALID_STAFF_ID, START_DATE, END_DATE, 0.0, "Holiday", LeaveStatus.PENDING
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Total days must be greater than zero");
    }

    @Test
    @DisplayName("You cannot create a LeaveRequestDTO with null required fields")
    void cannotCreateWithNullFields() {
        assertThatThrownBy(() -> new LeaveRequestDTO(
                null, VALID_STAFF_ID, START_DATE, END_DATE, 3.0, "Holiday", LeaveStatus.PENDING
        ))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("ID cannot be null");
    }
}
