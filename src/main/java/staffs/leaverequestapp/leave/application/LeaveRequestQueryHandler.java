package staffs.leaverequestapp.leave.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import staffs.leaverequestapp.leave.application.dto.LeaveRequestDTO;
import staffs.leaverequestapp.leave.application.mapper.LeaveRequestJpaToDTOMapper;
import staffs.leaverequestapp.leave.domain.LeaveStatus;
import staffs.leaverequestapp.leave.persistance.entities.LeaveRequestJpa;
import staffs.leaverequestapp.leave.persistance.repositories.LeaveRequestRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class LeaveRequestQueryHandler {
    private LeaveRequestRepository leaveRequestRepository;

    public List<LeaveRequestDTO> findLeaveRequestsByStaffId(UUID staffId) {
        List<LeaveRequestJpa> jpaList = leaveRequestRepository.findByStaffId(staffId);

        return jpaList.stream()
                .map(LeaveRequestJpaToDTOMapper::toLeaveRequestDTO)
                .collect(Collectors.toList());
    }

    public List<LeaveRequestDTO> findLeaveRequestsByStaffIds(List<UUID> staffIds) {
        return leaveRequestRepository.findByStaffIdIn(staffIds).stream()
                .map(LeaveRequestJpaToDTOMapper::toLeaveRequestDTO)
                .toList();
    }
}
