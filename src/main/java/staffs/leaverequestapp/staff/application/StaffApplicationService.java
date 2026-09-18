package staffs.leaverequestapp.staff.application;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import staffs.leaverequestapp.common.domain.FullName;
import staffs.leaverequestapp.common.domain.Identity;
import staffs.leaverequestapp.common.events.DomainEventManager;
import staffs.leaverequestapp.leave.application.mapper.LeaveAllowanceDomainToJpaMapper;
import staffs.leaverequestapp.leave.application.mapper.LeaveAllowanceJpaToDomainMapper;
import staffs.leaverequestapp.leave.application.mapper.LeaveRequestDomainToJpaMapper;
import staffs.leaverequestapp.leave.domain.LeaveAllowance;
import staffs.leaverequestapp.leave.domain.LeaveRequest;
import staffs.leaverequestapp.leave.persistance.entities.LeaveAllowanceJpa;
import staffs.leaverequestapp.leave.ui.SubmitLeaveRequestCommand;
import staffs.leaverequestapp.staff.application.mapper.StaffDomainToJpaMapper;
import staffs.leaverequestapp.staff.domain.EmploymentStatus;
import staffs.leaverequestapp.staff.domain.Organisation;
import staffs.leaverequestapp.staff.domain.Placement;
import staffs.leaverequestapp.staff.domain.StaffMember;
import staffs.leaverequestapp.staff.persistance.entities.StaffJpa;
import staffs.leaverequestapp.staff.persistance.repositories.StaffRepository;
import staffs.leaverequestapp.staff.ui.CreateStaffMemberCommand;

import java.util.UUID;

@Service
@AllArgsConstructor
public class StaffApplicationService {
    private final StaffRepository staffRepository;

    private final DomainEventManager domainEventManager;

    @Transactional
    public UUID createStaffMember(CreateStaffMemberCommand command) {
        Identity<StaffJpa> newStaffMemberId = Identity.generateId();

        StaffMember staffMember = StaffMember.hire(
                newStaffMemberId,
                new FullName(command.firstName(), command.surname()),
                command.email(),
                new Organisation(command.hireDate(), command.department(), command.lineManagerId()),
                new Placement(command.currentRole(), command.roleStartDate(), command.jobLevel(), command.employmentType())
        );

        StaffJpa staffJpa = StaffDomainToJpaMapper.toJpa(staffMember);

        staffRepository.save(staffJpa);

        domainEventManager.manageDomainEvents("StaffContext", staffMember.listOfDomainEvents());

        return staffMember.getStaffId();
    }
}
