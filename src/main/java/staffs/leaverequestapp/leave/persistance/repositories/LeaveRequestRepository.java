package staffs.leaverequestapp.leave.persistance.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import staffs.leaverequestapp.leave.domain.LeaveStatus;
import staffs.leaverequestapp.leave.persistance.entities.LeaveAllowanceJpa;
import staffs.leaverequestapp.leave.persistance.entities.LeaveRequestJpa;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface LeaveRequestRepository extends CrudRepository<LeaveRequestJpa, String> {
    List<LeaveRequestJpa> findByStaffId(UUID staffId);
    List<LeaveRequestJpa> findByStaffIdIn(List<UUID> staffIds);

    List<LeaveRequestJpa> findByStaffIdInAndStatusAndStartDateBetween(
            List<UUID> staffIds,
            LeaveStatus status,
            LocalDate startDate,
            LocalDate endDate);
    List<LeaveRequestJpa> findByStaffIdInAndStatusAndStartDateGreaterThanEqual(
            List<UUID> staffIds,
            LeaveStatus status,
            LocalDate startDate);

    List<LeaveRequestJpa> findByStaffIdInAndStatus(
            List<UUID> staffIds,
            LeaveStatus leaveStatus);

    List<LeaveRequestJpa> findByStatusAndStartDateBetween(
            LeaveStatus status,
            LocalDate startDate,
            LocalDate endDate);

    List<LeaveRequestJpa> findByStatusAndStartDateGreaterThanEqual(
            LeaveStatus status,
            LocalDate startDate);
    List<LeaveRequestJpa> findByStatus(LeaveStatus status);
}
