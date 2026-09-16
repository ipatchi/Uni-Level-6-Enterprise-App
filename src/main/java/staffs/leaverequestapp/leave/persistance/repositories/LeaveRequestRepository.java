package staffs.leaverequestapp.leave.persistance.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import staffs.leaverequestapp.leave.domain.LeaveStatus;
import staffs.leaverequestapp.leave.persistance.entities.LeaveAllowanceJpa;
import staffs.leaverequestapp.leave.persistance.entities.LeaveRequestJpa;

import java.util.List;
import java.util.UUID;

@Repository
public interface LeaveRequestRepository extends CrudRepository<LeaveRequestJpa, String> {
    List<LeaveRequestJpa> findByStaffId(UUID staffId);
    List<LeaveRequestJpa> findByStaffIdIn(List<UUID> staffIds);
}
