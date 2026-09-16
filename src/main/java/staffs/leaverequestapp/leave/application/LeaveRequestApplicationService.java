package staffs.leaverequestapp.leave.application;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import staffs.leaverequestapp.common.domain.Identity;
import staffs.leaverequestapp.leave.application.mapper.LeaveAllowanceDomainToJpaMapper;
import staffs.leaverequestapp.leave.application.mapper.LeaveAllowanceJpaToDomainMapper;
import staffs.leaverequestapp.leave.application.mapper.LeaveRequestDomainToJpaMapper;
import staffs.leaverequestapp.leave.application.mapper.LeaveRequestJpaToDomainMapper;
import staffs.leaverequestapp.leave.domain.LeaveAllowance;
import staffs.leaverequestapp.leave.domain.LeaveRequest;
import staffs.leaverequestapp.leave.persistance.entities.LeaveAllowanceJpa;
import staffs.leaverequestapp.leave.persistance.entities.LeaveRequestJpa;
import staffs.leaverequestapp.leave.persistance.repositories.LeaveAllowanceRepository;
import staffs.leaverequestapp.leave.persistance.repositories.LeaveRequestRepository;
import staffs.leaverequestapp.leave.ui.ApproveLeaveRequestCommand;
import staffs.leaverequestapp.leave.ui.RejectLeaveRequestCommand;
import staffs.leaverequestapp.leave.ui.SubmitLeaveRequestCommand;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class LeaveRequestApplicationService {
    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveAllowanceRepository leaveAllowanceRepository;

    public LeaveRequestApplicationService(LeaveRequestRepository leaveRequestRepository,
                                          LeaveAllowanceRepository leaveAllowanceRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.leaveAllowanceRepository = leaveAllowanceRepository;
    }

    @Transactional
    public String submitLeaveRequest(SubmitLeaveRequestCommand command) {
        int year = command.startDate().getYear();

        LeaveAllowanceJpa allowanceJpa = leaveAllowanceRepository
                .findByStaffId(command.staffId())
                .orElseThrow(() -> new IllegalArgumentException("No leave allowance found for staff member " + command.staffId()));

        LeaveAllowance leaveAllowance = LeaveAllowanceJpaToDomainMapper.map(allowanceJpa);

        Identity<LeaveRequest> newLeaveRequestId = Identity.generateId();
        LeaveRequest leaveRequest = new LeaveRequest(newLeaveRequestId, command.staffId(), command.startDate(), command.endDate(), command.reason());

        leaveAllowance.deductLeave(leaveRequest.getTotalDays());
        leaveAllowanceRepository.save(LeaveAllowanceDomainToJpaMapper.map(leaveAllowance));
        leaveRequestRepository.save(LeaveRequestDomainToJpaMapper.map(leaveRequest));

        return leaveRequest.id().id();
    }

    public void approveLeaveRequest(ApproveLeaveRequestCommand command) {

        LeaveRequestJpa leaveRequestJpa = leaveRequestRepository
                .findById(command.leaveRequestId())
                .orElseThrow(() -> new IllegalArgumentException("Leave request not found with id" + command.leaveRequestId()));

        LeaveRequest leaveRequest = LeaveRequestJpaToDomainMapper.map(leaveRequestJpa);

        leaveRequest.approveRequest();

        leaveRequestRepository.save(LeaveRequestDomainToJpaMapper.map(leaveRequest));
    }

    public void rejectLeaveRequest(RejectLeaveRequestCommand command) {

        LeaveRequestJpa leaveRequestJpa = leaveRequestRepository
                .findById(command.leaveRequestId())
                .orElseThrow(() -> new IllegalArgumentException("Leave request not found with id" + command.leaveRequestId()));

        LeaveRequest leaveRequest = LeaveRequestJpaToDomainMapper.map(leaveRequestJpa);

        leaveRequest.rejectRequest();

        int year = leaveRequest.getStartDate().getYear();
        UUID staffId = leaveRequest.getStaffId();
        LeaveAllowanceJpa allowanceJpa = leaveAllowanceRepository
                .findByStaffId(staffId)
                .orElseThrow(() -> new IllegalArgumentException("No leave allowance found for staff member " + staffId));

        LeaveAllowance leaveAllowance = LeaveAllowanceJpaToDomainMapper.map(allowanceJpa);
        leaveAllowance.restoreLeave(leaveRequest.getTotalDays());

        leaveRequestRepository.save(LeaveRequestDomainToJpaMapper.map(leaveRequest));
        leaveAllowanceRepository.save(LeaveAllowanceDomainToJpaMapper.map(leaveAllowance));
    }
}
