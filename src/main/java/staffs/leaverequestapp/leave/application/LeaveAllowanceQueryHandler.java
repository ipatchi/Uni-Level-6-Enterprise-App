package staffs.leaverequestapp.leave.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import staffs.leaverequestapp.leave.application.dto.LeaveAllowanceDTO;
import staffs.leaverequestapp.leave.application.mapper.LeaveAllowanceJpaToDTOMapper;
import staffs.leaverequestapp.leave.persistance.entities.LeaveAllowanceJpa;
import staffs.leaverequestapp.leave.persistance.repositories.LeaveAllowanceRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class LeaveAllowanceQueryHandler {
    private LeaveAllowanceRepository leaveAllowanceRepository;

    public LeaveAllowanceDTO findLeaveAllowanceByUserId(UUID staffId){
        return leaveAllowanceRepository
                .findByStaffId(staffId)
                .map(LeaveAllowanceJpaToDTOMapper::toLeaveAllowanceDTO)
                .orElseThrow(() -> new IllegalArgumentException("Allowance could not be found"));
    }

    public LeaveAllowanceDTO findLeaveAllowanceByIdentityId(String identityId){
        return leaveAllowanceRepository
                .findByIdentityId(identityId)
                .map(LeaveAllowanceJpaToDTOMapper::toLeaveAllowanceDTO)
                .orElseThrow(() -> new IllegalArgumentException("Allowance could not be found"));
    }

    public List<LeaveAllowanceDTO> findLeaveAllowanceByManagerId(UUID managerId){
        List<LeaveAllowanceJpa> jpaList = leaveAllowanceRepository.findByManagerId(managerId);
        return jpaList.stream()
                .map(LeaveAllowanceJpaToDTOMapper::toLeaveAllowanceDTO)
                .collect(Collectors.toList());
    }


}
