package staffs.leaverequestapp.leave;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import staffs.leaverequestapp.common.domain.FullName;
import staffs.leaverequestapp.leave.application.dto.LeaveAllowanceDTO;
import staffs.leaverequestapp.leave.application.mapper.LeaveAllowanceJpaToDTOMapper;
import staffs.leaverequestapp.leave.persistance.entities.LeaveAllowanceJpa;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Leave allowance JPA to DTO mapper unit tests")
class LeaveAllowanceJpaToDTOMapperTest {

    @Test
    @DisplayName("You can map a valid LeaveAllowanceJpa to a LeaveAllowanceDTO")
    void mapsCorrectly() {
        LeaveAllowanceJpa jpa = new LeaveAllowanceJpa();
        jpa.setId(UUID.randomUUID().toString());
        jpa.setStaffId(UUID.randomUUID());
        jpa.setManagerId(UUID.randomUUID());
        jpa.setFullName(new FullName("test","data"));
        jpa.setYear(2026);
        jpa.setTotalAllowance(25.0);
        jpa.setUsedAllowance(10.0);

        LeaveAllowanceDTO dto = LeaveAllowanceJpaToDTOMapper.toLeaveAllowanceDTO(jpa);

        assertAll(
                () -> assertEquals(UUID.fromString(jpa.getId()), dto.id()),
                () -> assertEquals(jpa.getStaffId(), dto.staffId()),
                () -> assertEquals(jpa.getManagerId(), dto.managerId()),
                () -> assertEquals(jpa.getYear(), dto.year()),
                () -> assertEquals(jpa.getTotalAllowance(), dto.totalAllowance()),
                () -> assertEquals(jpa.getUsedAllowance(), dto.usedAllowance())
        );
    }

    @Test
    @DisplayName("You cannot map a null JPA entity")
    void throwsOnNullEntity() {
        assertThatThrownBy(() -> LeaveAllowanceJpaToDTOMapper.toLeaveAllowanceDTO(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Leave allowance JPA entity cannot be null");
    }
}
