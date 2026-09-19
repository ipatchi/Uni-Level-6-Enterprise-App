package staffs.leaverequestapp.leave.application.events;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import staffs.leaverequestapp.common.events.StaffMemberAmendedEvent;
import staffs.leaverequestapp.leave.application.LeaveAllowanceApplicationService;
import staffs.leaverequestapp.leave.ui.AmmendLeaveAllowanceDetailsCommand;

@Component
@Slf4j
@AllArgsConstructor
public class StaffMemberAmendedListener {
    private LeaveAllowanceApplicationService leaveAllowanceApplicationService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(StaffMemberAmendedEvent event){
        log.info("Staff Member Amended event received for staff ID: {}", event.staffId());

        String firstName = event.fullName() != null ? event.fullName().firstName() : null;
        String surname = event.fullName() != null ? event.fullName().surname() : null;

        AmmendLeaveAllowanceDetailsCommand command = new AmmendLeaveAllowanceDetailsCommand(
                event.staffId(),
                firstName,
                surname,
                event.managerId()
        );

        leaveAllowanceApplicationService.ammendLeaveAllowanceDetails(command);
    }
}
