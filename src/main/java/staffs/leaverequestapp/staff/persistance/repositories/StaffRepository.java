package staffs.leaverequestapp.staff.persistance.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import staffs.leaverequestapp.staff.domain.StaffMember;
import staffs.leaverequestapp.staff.persistance.entities.StaffJpa;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StaffRepository extends CrudRepository<StaffJpa, String> {
    Optional<StaffJpa> findByStaffId(UUID staffId);
    Optional<StaffJpa> findByIdentityId(String identityId);
}
