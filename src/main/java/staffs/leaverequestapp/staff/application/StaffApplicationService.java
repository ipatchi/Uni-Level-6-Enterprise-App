package staffs.leaverequestapp.staff.application;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import staffs.leaverequestapp.common.domain.FullName;
import staffs.leaverequestapp.common.domain.Identity;
import staffs.leaverequestapp.common.events.infra.DomainEventManager;
import staffs.leaverequestapp.common.events.StaffMemberAmendedEvent;
import staffs.leaverequestapp.staff.application.mapper.StaffDomainToJpaMapper;
import staffs.leaverequestapp.staff.application.mapper.StaffMemberJpaToDomainMapper;
import staffs.leaverequestapp.staff.domain.Organisation;
import staffs.leaverequestapp.staff.domain.Placement;
import staffs.leaverequestapp.staff.domain.StaffMember;
import staffs.leaverequestapp.staff.persistance.entities.StaffJpa;
import staffs.leaverequestapp.staff.persistance.repositories.StaffRepository;
import staffs.leaverequestapp.staff.ui.AmmendStaffMemberCommand;
import staffs.leaverequestapp.staff.ui.CreateStaffMemberCommand;

import java.util.UUID;

@Service
@AllArgsConstructor
public class StaffApplicationService {
    private final StaffRepository staffRepository;

    private final DomainEventManager domainEventManager;

    @Transactional
    public UUID createStaffMember(CreateStaffMemberCommand command) {
        Identity<StaffMember> newStaffMemberId = Identity.generateId();

        StaffMember staffMember = StaffMember.hire(
                newStaffMemberId,
                new FullName(command.firstName(), command.surname()),
                command.email(),
                new Organisation(command.hireDate(), command.department(), command.managerId()),
                new Placement(command.currentRole(), command.roleStartDate(), command.jobLevel(), command.employmentType()),
                command.managerId(),
                command.identityId()
        );

        StaffJpa staffJpa = StaffDomainToJpaMapper.toJpa(staffMember);

        staffRepository.save(staffJpa);

        domainEventManager.manageDomainEvents("StaffContext", staffMember.listOfDomainEvents());

        return staffMember.getStaffId();
    }

    @Transactional
    public void ammendStaffMember(AmmendStaffMemberCommand command) {
        StaffJpa staffJpa = staffRepository.findById(command.staffId().toString())
                .orElseThrow(() -> new IllegalArgumentException("Staff member not found with ID: " + command.staffId()));

        StaffMember staff = StaffMemberJpaToDomainMapper.map(staffJpa);

       staff.updateDetails(
               command.newFirstName(),
               command.newSurname(),
               command.newEmail(),
               command.newDepartment(),
               command.newManagerId(),
               command.newRole(),
               command.newJobLevel(),
               command.newEmploymentType(),
               command.newStatus()
       );

        staffRepository.save(StaffDomainToJpaMapper.toJpa(staff));

        FullName updatedFullName = new FullName(staff.getIdentity().firstName(), staff.getIdentity().surname());

        StaffMemberAmendedEvent event = new StaffMemberAmendedEvent(
                staff.getStaffId(),
                staff.getManagerId(),
                updatedFullName
        );

        domainEventManager.manageDomainEvents("StaffContext", staff.listOfDomainEvents());
    }
}
