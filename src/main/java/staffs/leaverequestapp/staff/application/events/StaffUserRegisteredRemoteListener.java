package staffs.leaverequestapp.staff.application.events;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import staffs.leaverequestapp.common.events.StaffMemberHiredEvent;
import staffs.leaverequestapp.common.events.UserRegisteredEvent;
import staffs.leaverequestapp.staff.application.StaffApplicationService;
import staffs.leaverequestapp.staff.domain.EmploymentStatus;
import staffs.leaverequestapp.staff.domain.EmploymentType;
import staffs.leaverequestapp.staff.persistance.repositories.StaffRepository;
import staffs.leaverequestapp.staff.ui.CreateStaffMemberCommand;

import java.time.LocalDate;
import java.util.UUID;

@Component
@Slf4j
@AllArgsConstructor
public class StaffUserRegisteredRemoteListener {
    private StaffApplicationService staffApplicationService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(UserRegisteredEvent event){  // Listen for Staff Member Hired event
        log.info("User registered with identity id: {}", event.identityId());

        LocalDate eventDate = java.time.LocalDate.ofInstant(
                event.occurredOn(),
                java.time.ZoneId.systemDefault()
        );

        CreateStaffMemberCommand command = new CreateStaffMemberCommand(
                event.firstName(),
                event.surname(),
                event.email(),
                eventDate,
                "Department Not Set",
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "Current role not set",
                LocalDate.now(),
                "Job level not set",
                EmploymentType.FULL_TIME,
                EmploymentStatus.ACTIVE,
                event.identityId()
        );

        staffApplicationService.createStaffMember(command);
    }

}
