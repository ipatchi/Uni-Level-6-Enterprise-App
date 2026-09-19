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
import staffs.leaverequestapp.leave.domain.LeaveStatus;
import staffs.leaverequestapp.leave.domain.exceptions.LeaveRequestCannotBeCancelledByProxyException;
import staffs.leaverequestapp.leave.domain.exceptions.LeaveRequestHasBeenCancelledException;
import staffs.leaverequestapp.leave.persistance.entities.LeaveAllowanceJpa;
import staffs.leaverequestapp.leave.persistance.entities.LeaveRequestJpa;
import staffs.leaverequestapp.leave.persistance.repositories.LeaveAllowanceRepository;
import staffs.leaverequestapp.leave.persistance.repositories.LeaveRequestRepository;
import staffs.leaverequestapp.leave.ui.ApproveLeaveRequestCommand;
import staffs.leaverequestapp.leave.ui.CancelLeaveRequestCommand;
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

    @Transactional
    public String approveLeaveRequest(ApproveLeaveRequestCommand command) {

        LeaveRequestJpa leaveRequestJpa = leaveRequestRepository
                .findById(command.leaveRequestId())
                .orElseThrow(() -> new IllegalArgumentException("Leave request not found with id" + command.leaveRequestId()));

        LeaveRequest leaveRequest = LeaveRequestJpaToDomainMapper.map(leaveRequestJpa);

        leaveRequest.approveRequest();

        leaveRequestRepository.save(LeaveRequestDomainToJpaMapper.map(leaveRequest));

        return leaveRequest.id().id();
    }

    @Transactional
    public String rejectLeaveRequest(RejectLeaveRequestCommand command) {

        LeaveRequestJpa leaveRequestJpa = leaveRequestRepository
                .findById(command.leaveRequestId())
                .orElseThrow(() -> new IllegalArgumentException("Leave request not found with id" + command.leaveRequestId()));

        LeaveRequest leaveRequest = LeaveRequestJpaToDomainMapper.map(leaveRequestJpa);

        leaveRequest.rejectRequest();

        UUID staffId = leaveRequest.getStaffId();
        LeaveAllowanceJpa allowanceJpa = leaveAllowanceRepository
                .findByStaffId(staffId)
                .orElseThrow(() -> new IllegalArgumentException("No leave allowance found for staff member " + staffId));

        LeaveAllowance leaveAllowance = LeaveAllowanceJpaToDomainMapper.map(allowanceJpa);
        leaveAllowance.restoreLeave(leaveRequest.getTotalDays());

        leaveRequestRepository.save(LeaveRequestDomainToJpaMapper.map(leaveRequest));
        leaveAllowanceRepository.save(LeaveAllowanceDomainToJpaMapper.map(leaveAllowance));

        return leaveRequest.id().id();
    }

    @Transactional
    public String cancelLeaveRequest(CancelLeaveRequestCommand command) {
        LeaveRequestJpa leaveRequestJpa = leaveRequestRepository
                .findById(command.leaveRequestId())
                .orElseThrow(() -> new IllegalArgumentException("Leave request not found with id" + command.leaveRequestId()));

        LeaveRequest leaveRequest = LeaveRequestJpaToDomainMapper.map(leaveRequestJpa);

        LeaveStatus previousStatus = leaveRequest.getStatus();

        if (previousStatus == LeaveStatus.CANCELLED) {
            throw new LeaveRequestHasBeenCancelledException("Request already cancelled");
        }

        UUID staffId = leaveRequest.getStaffId();
        if (!staffId.equals(command.staffId())) {
            throw new LeaveRequestCannotBeCancelledByProxyException("You can only cancel your own leave requests");
        }

        leaveRequest.cancelRequest(command.staffId());
        leaveRequestRepository.save(LeaveRequestDomainToJpaMapper.map(leaveRequest));

        //When a request is created it already deducts the amount, when it is rejected the amount is refunded
        if (previousStatus != LeaveStatus.REJECTED){
            LeaveAllowanceJpa allowanceJpa = leaveAllowanceRepository
                    .findByStaffId(staffId)
                    .orElseThrow(() -> new IllegalArgumentException("No leave allowance found for staff member " + staffId));

            LeaveAllowance leaveAllowance = LeaveAllowanceJpaToDomainMapper.map(allowanceJpa);
            leaveAllowance.restoreLeave(leaveRequest.getTotalDays());

            leaveAllowanceRepository.save(LeaveAllowanceDomainToJpaMapper.map(leaveAllowance));
        }

        return leaveRequest.id().id();
    }
}
