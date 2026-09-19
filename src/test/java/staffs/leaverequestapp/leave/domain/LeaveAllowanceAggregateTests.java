package staffs.leaverequestapp.leave.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import staffs.leaverequestapp.common.domain.FullName;
import staffs.leaverequestapp.common.domain.Identity;
import staffs.leaverequestapp.leave.domain.LeaveAllowance;
import staffs.leaverequestapp.leave.domain.exceptions.InsufficientLeaveBalanceException;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("LeaveAllowance unit tests")
public class LeaveAllowanceAggregateTests {

    private Identity<LeaveAllowance> allowanceIdentity;
    private UUID validStaffId;
    private UUID validManagerId;
    private FullName validFullName;
    private final int VALID_YEAR = 2026;
    private final double STANDARD_ALLOWANCE = 28.0;
    private final String validIdentityId = "firebase-identity";

    @BeforeEach
    void setUp() {
        allowanceIdentity = Identity.of("allowance-123");
        validStaffId = UUID.fromString("12345678-1234-1234-1234-123456789012");
        validManagerId = UUID.fromString("87654321-4321-4321-4321-210987654321");
        validFullName = new FullName("John", "Doe");
    }

    private LeaveAllowance createValidAllowance() {
        return new LeaveAllowance(
                allowanceIdentity,
                validStaffId,
                validManagerId,
                VALID_YEAR,
                STANDARD_ALLOWANCE,
                validFullName,
                validIdentityId
        );
    }

    @Test
    @DisplayName("You can create a LeaveAllowance when all arguments are valid")
    void objectCreatedWithValidDetails() {
        LeaveAllowance allowance = createValidAllowance();

        assertAll(
                () -> assertEquals(allowanceIdentity, allowance.id()),
                () -> assertEquals(validStaffId, allowance.getStaffId()),
                () -> assertEquals(validFullName, allowance.getFullName()),
                () -> assertEquals(validManagerId, allowance.getManagerId()),
                () -> assertEquals(VALID_YEAR, allowance.getYear()),
                () -> assertEquals(STANDARD_ALLOWANCE, allowance.getTotalAllowance()),
                () -> assertEquals(0.0, allowance.getUsedAllowance()),
                () -> assertEquals(STANDARD_ALLOWANCE, allowance.getRemainingBalance())
        );
    }

    @Test
    @DisplayName("You cannot create a leave allowance with a zero or negative total amount")
    void invalidAllowanceAmountIsRejected() {
        assertThatThrownBy(() -> new LeaveAllowance(allowanceIdentity,
                validStaffId,
                validManagerId,
                VALID_YEAR,
                0.0,
                validFullName,
                validIdentityId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Total allowance must be greater than zero");

        assertThatThrownBy(() -> new LeaveAllowance(allowanceIdentity,
                validStaffId,
                validManagerId,
                VALID_YEAR,
                -5.0,
                validFullName,
                validIdentityId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Total allowance must be greater than zero");
    }

    @Test
    @DisplayName("Leave can be deducted successfully and updates remaining balance")
    void leaveDeductedSuccessfully() {
        LeaveAllowance allowance = createValidAllowance();

        allowance.deductLeave(5.0);

        assertAll(
                () -> assertEquals(5.0, allowance.getUsedAllowance()),
                () -> assertEquals(23.0, allowance.getRemainingBalance())
        );
    }

    @Test
    @DisplayName("An exception is thrown when attempting to deduct more than the remaining balance")
    void cannotDeductMoreThanBalance() {
        LeaveAllowance allowance = createValidAllowance();
        allowance.deductLeave(25.0);
        assertThatThrownBy(() -> allowance.deductLeave(4.0))
                .isInstanceOf(InsufficientLeaveBalanceException.class)
                .hasMessage("Cannot deduct 4.0 days. Only 3.0 days remaining.");
    }

    @Test
    @DisplayName("Leave can be restored successfully (e.g., when a request is rejected or cancelled)")
    void leaveRestoredSuccessfully() {
        LeaveAllowance allowance = createValidAllowance();

        allowance.deductLeave(10.0);

        allowance.restoreLeave(4.0);

        assertAll(
                () -> assertEquals(6.0, allowance.getUsedAllowance()),
                () -> assertEquals(22.0, allowance.getRemainingBalance())
        );
    }
}
