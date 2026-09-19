package staffs.leaverequestapp.leave.application;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import staffs.leaverequestapp.common.domain.FullName;
import staffs.leaverequestapp.common.domain.Identity;
import staffs.leaverequestapp.leave.application.mapper.LeaveAllowanceDomainToJpaMapper;
import staffs.leaverequestapp.leave.application.mapper.LeaveAllowanceJpaToDomainMapper;
import staffs.leaverequestapp.leave.domain.LeaveAllowance;
import staffs.leaverequestapp.leave.domain.exceptions.LeaveAllowanceNotFoundException;
import staffs.leaverequestapp.leave.persistance.entities.LeaveAllowanceJpa;
import staffs.leaverequestapp.leave.persistance.repositories.LeaveAllowanceRepository;
import staffs.leaverequestapp.leave.persistance.repositories.LeaveRequestRepository;
import staffs.leaverequestapp.leave.ui.*;

import java.util.Optional;

@Service
public class LeaveAllowanceApplicationService {
    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveAllowanceRepository leaveAllowanceRepository;

    public LeaveAllowanceApplicationService(LeaveRequestRepository leaveRequestRepository,
                                            LeaveAllowanceRepository leaveAllowanceRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.leaveAllowanceRepository = leaveAllowanceRepository;
    }

    @Transactional
    public String addLeaveAllowance(AddLeaveAllowanceCommand command) {
        Identity<LeaveAllowance> newLeaveAllowanceId = Identity.generateId();
        LeaveAllowance leaveAllowance = new LeaveAllowance(
                newLeaveAllowanceId,
                command.staffId(),
                command.managerId(),
                command.year(),
                command.totalAllowance(),
                command.fullName(),
                command.identityId());

        leaveAllowanceRepository.save(LeaveAllowanceDomainToJpaMapper.map(leaveAllowance));

        return leaveAllowance.id().id();
    }

    @Transactional
    public String ammendLeaveAllowanceDetails(AmmendLeaveAllowanceDetailsCommand command) {
        LeaveAllowanceJpa allowanceJpa = leaveAllowanceRepository.findByStaffId(command.staffId())
                .orElseThrow(() -> new IllegalArgumentException("No leave allowance found for staff member " + command.staffId()));

        LeaveAllowance leaveAllowance = LeaveAllowanceJpaToDomainMapper.map(allowanceJpa);

        FullName updatedFullName = null;
        if (command.newFirstName() != null || command.newSurname() != null) {
            String firstName = command.newFirstName() != null ? command.newFirstName() : leaveAllowance.getFullName().firstName();
            String surname = command.newSurname() != null ? command.newSurname() : leaveAllowance.getFullName().surname();

            updatedFullName = new FullName(firstName, surname);
        }

        leaveAllowance.updateStaffDetails(
                updatedFullName,
                command.newManagerId()
        );

        leaveAllowanceRepository.save(LeaveAllowanceDomainToJpaMapper.map(leaveAllowance));

        return leaveAllowance.id().id();
    }

    @Transactional
    public LeaveAllowanceJpa ammendLeaveAllowance(AmmendLeaveAllowanceCommand command) {
        leaveAllowanceRepository.findByIdentityId(command.identityId())
                .orElseThrow(() -> new LeaveAllowanceNotFoundException(
                        "No leave allowance found for identity " + command.identityId()));

        Optional<LeaveAllowanceJpa> allowanceJpa = leaveAllowanceRepository.findByStaffId(command.staffId());
        if (allowanceJpa.isEmpty()) {
            throw new LeaveAllowanceNotFoundException("No leave allowance found for staff member " + command.staffId());
        }

        LeaveAllowance leaveAllowance = LeaveAllowanceJpaToDomainMapper.map(allowanceJpa.get());

        leaveAllowance.amendTotal(command.totalAllowance());

        return leaveAllowanceRepository.save(LeaveAllowanceDomainToJpaMapper.map(leaveAllowance));
    }


}
