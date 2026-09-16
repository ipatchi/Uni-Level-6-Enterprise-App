package staffs.leaverequestapp.leave;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import staffs.leaverequestapp.common.domain.Identity;
import staffs.leaverequestapp.leave.domain.LeaveRequest;
import staffs.leaverequestapp.leave.domain.LeaveStatus;
import staffs.leaverequestapp.leave.domain.exceptions.LeaveRequestCannotBeCancelledByProxyException;
import staffs.leaverequestapp.leave.domain.exceptions.LeaveRequestCannotBeRejectedException;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("LeaveRequest unit tests")
public class LeaveRequestAggregateTests {

    private Identity<LeaveRequest> requestIdentity;
    private UUID validStaffId;
    private LocalDate validStartDate;
    private LocalDate validEndDate;
    private double validTotalDays;
    private final String VALID_REASON = "Annual family holiday";

    @BeforeEach
    void setUp() {
        requestIdentity = Identity.of("req-12345678");
        validStaffId = UUID.fromString("12345678-1234-1234-1234-123456789012");
        validStartDate = LocalDate.now().plusDays(7);
        validEndDate = LocalDate.now().plusDays(14);
        validTotalDays = 8;
    }

    private LeaveRequest createValidLeaveRequest() {
        return new LeaveRequest(
                requestIdentity,
                validStaffId,
                validStartDate,
                validEndDate,
                VALID_REASON
        );
    }

    @Test
    @DisplayName("You can create a LeaveRequest when all arguments are valid")
    void objectCreatedWithValidDetails() {
        LeaveRequest request = createValidLeaveRequest();

        assertAll(
                () -> assertEquals(requestIdentity, request.id()),
                () -> assertEquals(validStaffId, request.getStaffId()),
                () -> assertEquals(validStartDate, request.getStartDate()),
                () -> assertEquals(8.0, request.getTotalDays()),
                () -> assertEquals(validEndDate, request.getEndDate()),
                () -> assertEquals(VALID_REASON, request.getReason()),
                () -> assertEquals(LeaveStatus.PENDING, request.getStatus())
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("You cannot create a leave request with a null or empty reason")
    void nullOrEmptyReasonIsRejected(String invalidReason) {
        assertThatThrownBy(() -> new LeaveRequest(
                requestIdentity,
                validStaffId,
                validStartDate,
                validEndDate,
                invalidReason))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Reason cannot be empty");
    }

    @Test
    @DisplayName("You cannot create a leave request with an end date before the start date")
    void invalidDatesAreRejected() {
        LocalDate invalidEndDate = validStartDate.minusDays(2);

        assertThatThrownBy(() -> new LeaveRequest(
                requestIdentity,
                validStaffId,
                validStartDate,
                invalidEndDate,
                VALID_REASON))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("End date cannot be before start date");
    }

    @Test
    @DisplayName("A leave request status can be changed from pending to approved")
    void statusChangesToApprovedWhenApproved() {
        LeaveRequest request = createValidLeaveRequest();

        request.approveRequest();

        assertEquals(LeaveStatus.APPROVED, request.getStatus());
    }

    @Test
    @DisplayName("A leave request status cannot be rejected once approved")
    void statusCannotBeRejectedOnceApproved() {
        LeaveRequest request = createValidLeaveRequest();
        request.approveRequest();

        assertThatThrownBy(request::rejectRequest)
                .isInstanceOf(LeaveRequestCannotBeRejectedException.class)
                .hasMessage("Approved requests cannot be rejected");
    }

    @Test
    @DisplayName("A leave request cannot be cancelled by a different staff member")
    void cannotBeCancelledByProxy() {
        LeaveRequest request = createValidLeaveRequest();
        UUID differentStaffId = UUID.randomUUID();

        assertThatThrownBy(() -> request.cancelRequest(differentStaffId))
                .isInstanceOf(LeaveRequestCannotBeCancelledByProxyException.class)
                .hasMessage("You can only cancel your own leave requests.");
    }

    @Test
    @DisplayName("A single-day leave request correctly calculates total days as 1")
    void singleDayLeaveRequestCalculatesCorrectly() {
        LocalDate singleDay = LocalDate.now().plusDays(5);

        LeaveRequest request = new LeaveRequest(
                requestIdentity,
                validStaffId,
                singleDay,
                singleDay, // Same day start and end
                "Dentist appointment"
        );

        assertEquals(1.0, request.getTotalDays());
    }

    /* Note: Uncomment this test once you add the 'addDomainEvent(new LeaveRequestSubmittedEvent(...))'
       to your LeaveRequest constructor! */

    // @Test
    // @DisplayName("LeaveRequestSubmittedEvent raised when a request is created")
    // void eventIsPublishedOnCreation() {
    //     LeaveRequest request = createValidLeaveRequest();
    //
    //     List<Event> domainEvents = request.listOfDomainEvents();
    //
    //     assertAll(
    //             () -> assertNotNull(domainEvents),
    //             () -> assertEquals(1, domainEvents.size()),
    //             () -> assertInstanceOf(LeaveRequestSubmittedEvent.class, domainEvents.getFirst())
    //     );
    //
    //     LeaveRequestSubmittedEvent event = (LeaveRequestSubmittedEvent) domainEvents.getFirst();
    //
    //     assertAll(
    //             () -> assertEquals(requestIdentity.id(), event.leaveRequestId()),
    //             () -> assertEquals(validStaffId, event.staffId())
    //     );
    // }
}
