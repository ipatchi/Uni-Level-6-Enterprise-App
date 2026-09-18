package staffs.leaverequestapp.staff.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import staffs.leaverequestapp.staff.application.dto.StaffDTO;
import staffs.leaverequestapp.staff.application.mapper.StaffJpaToDTOMapper;
import staffs.leaverequestapp.staff.persistance.entities.StaffJpa;
import staffs.leaverequestapp.staff.persistance.repositories.StaffRepository;

import java.util.UUID;

@Service
@AllArgsConstructor
public class StaffQueryHandler
{
    private final StaffRepository staffRepository;

    public StaffDTO getStaffById(UUID staffId) {
        StaffJpa staffJpa = staffRepository.findById(staffId.toString())
                .orElseThrow(() -> new IllegalArgumentException("Staff member not found with ID: " + staffId));;
        return StaffJpaToDTOMapper.toStaffDTO(staffJpa);
    }
}
