package staffs.leaverequestapp.leave.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import staffs.leaverequestapp.leave.application.dto.LeaveRequestDTO;
import staffs.leaverequestapp.leave.application.mapper.LeaveRequestJpaToDTOMapper;
import staffs.leaverequestapp.leave.domain.LeaveStatus;
import staffs.leaverequestapp.leave.persistance.entities.LeaveRequestJpa;
import staffs.leaverequestapp.leave.persistance.repositories.LeaveRequestRepository;

import java.time.LocalDate;
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

    public List<LeaveRequestDTO> findOutstandingLeaveRequestsByStaffIdsAndDates(List<UUID> staffIds, LocalDate startDate, LocalDate endDate) {
        if (staffIds == null || staffIds.isEmpty()) {
            return List.of();
        }

        List<LeaveRequestJpa> jpaList;

        if (startDate != null && endDate != null) {
            jpaList = leaveRequestRepository.findByStaffIdInAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                    staffIds, LeaveStatus.PENDING, endDate, startDate);
        } else if (startDate != null) {
            jpaList = leaveRequestRepository.findByStaffIdInAndStatusAndEndDateGreaterThanEqual(
                    staffIds, LeaveStatus.PENDING, startDate);
        } else if (endDate != null) {
            jpaList = leaveRequestRepository.findByStaffIdInAndStatusAndStartDateLessThanEqual(
                    staffIds, LeaveStatus.PENDING, endDate);
        } else {
            jpaList = leaveRequestRepository.findByStaffIdInAndStatus(staffIds, LeaveStatus.PENDING);
        }

        return jpaList.stream()
                .map(LeaveRequestJpaToDTOMapper::toLeaveRequestDTO)
                .toList();
    }

    public List<LeaveRequestDTO> findAllOutstandingLeaveRequestsByDates(LocalDate startDate, LocalDate endDate) {
        List<LeaveRequestJpa> jpaList;
        if (startDate != null && endDate != null) {
            jpaList = leaveRequestRepository.findByStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                    LeaveStatus.PENDING, endDate, startDate);
        } else if (startDate != null) {
            jpaList = leaveRequestRepository.findByStatusAndEndDateGreaterThanEqual(LeaveStatus.PENDING, startDate);
        } else if (endDate != null) {
            jpaList = leaveRequestRepository.findByStatusAndStartDateLessThanEqual(LeaveStatus.PENDING, endDate);
        } else {
            jpaList = leaveRequestRepository.findByStatus(LeaveStatus.PENDING);
        }
        return jpaList.stream()
                .map(LeaveRequestJpaToDTOMapper::toLeaveRequestDTO)
                .toList();
    }
}
