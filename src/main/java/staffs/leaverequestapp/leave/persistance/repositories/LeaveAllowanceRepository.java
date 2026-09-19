package staffs.leaverequestapp.leave.persistance.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import staffs.leaverequestapp.leave.persistance.entities.LeaveAllowanceJpa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LeaveAllowanceRepository extends CrudRepository<LeaveAllowanceJpa, String> {
    Optional<LeaveAllowanceJpa> findByStaffId(UUID staffId);
    Optional<LeaveAllowanceJpa> findByIdentityId(String identityId);
    List<LeaveAllowanceJpa> findByManagerId(UUID managerId);
}
