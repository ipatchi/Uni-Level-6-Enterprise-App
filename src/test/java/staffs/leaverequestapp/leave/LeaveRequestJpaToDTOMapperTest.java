package staffs.leaverequestapp.leave;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import staffs.leaverequestapp.leave.application.dto.LeaveRequestDTO;
import staffs.leaverequestapp.leave.application.mapper.LeaveRequestJpaToDTOMapper;
import staffs.leaverequestapp.leave.domain.LeaveStatus;
import staffs.leaverequestapp.leave.persistance.entities.LeaveRequestJpa;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Leave Request JPA to DTO Mapper unit tests")
class LeaveRequestJpaToDTOMapperTest {

    @Test
    @DisplayName("You can map a valid LeaveRequestJpa to a LeaveRequestDTO")
    void mapsCorrectly() {
        // Arrange
        LeaveRequestJpa jpa = new LeaveRequestJpa();
        jpa.setId("REQ-123");
        jpa.setStaffId(UUID.randomUUID());
        jpa.setStartDate(LocalDate.now());
        jpa.setEndDate(LocalDate.now().plusDays(2));
        jpa.setTotalDays(3.0);
        jpa.setReason("Vacation");
        jpa.setStatus(LeaveStatus.APPROVED);

        // Act
        LeaveRequestDTO dto = LeaveRequestJpaToDTOMapper.toLeaveRequestDTO(jpa);

        // Assert
        assertAll(
                () -> assertEquals(jpa.getId(), dto.id()),
                () -> assertEquals(jpa.getStaffId(), dto.staffId()),
                () -> assertEquals(jpa.getTotalDays(), dto.totalDays()),
                () -> assertEquals(jpa.getStatus(), dto.status())
        );
    }

    @Test
    @DisplayName("Mapping throws an exception when the JPA entity is null")
    void throwsOnNullEntity() {
        assertThatThrownBy(() -> LeaveRequestJpaToDTOMapper.toLeaveRequestDTO(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Leave request JPA entity cannot be null");
    }
}
