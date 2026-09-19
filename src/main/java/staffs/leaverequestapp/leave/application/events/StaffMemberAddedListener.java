package staffs.leaverequestapp.leave.application.events;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import staffs.leaverequestapp.common.events.StaffMemberHiredEvent;
import staffs.leaverequestapp.leave.application.LeaveAllowanceApplicationService;
import staffs.leaverequestapp.leave.ui.AddLeaveAllowanceCommand;

import java.util.Calendar;

@Component
@Slf4j
@AllArgsConstructor
public class StaffMemberAddedListener {
    private LeaveAllowanceApplicationService leaveAllowanceApplicationService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(StaffMemberHiredEvent event){  // Listen for Staff Member Hired event
        log.info("Staff Member Hired event received for staff ID: {}", event.staffId());

        int eventYear = java.time.LocalDateTime.ofInstant(
                event.occurredOn(),
                java.time.ZoneId.systemDefault()
        ).getYear();

        AddLeaveAllowanceCommand command = new AddLeaveAllowanceCommand(
                event.staffId(),
                event.managerId(),
                eventYear,
                25,
                event.fullName(),
                event.identityId()
        );
        leaveAllowanceApplicationService.addLeaveAllowance(command);
    }
}
