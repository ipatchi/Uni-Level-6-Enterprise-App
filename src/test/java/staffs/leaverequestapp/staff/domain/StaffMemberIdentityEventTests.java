package staffs.leaverequestapp.staff.domain;

import org.junit.jupiter.api.Test;
import staffs.leaverequestapp.common.domain.FullName;
import staffs.leaverequestapp.common.domain.Identity;
import staffs.leaverequestapp.common.events.StaffMemberHiredEvent;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class StaffMemberIdentityEventTests {

    @Test
    void hiringPublishesStaffIdAndIamIdentityId() {
        String identityId = "firebase-staff";

        StaffMember staffMember = StaffMember.hire(
                Identity.generateId(),
                new FullName("Test", "User"),
                "test.user@example.com",
                new Organisation(LocalDate.now(), "Engineering", null),
                new Placement("Developer", LocalDate.now(), "L2", EmploymentType.FULL_TIME),
                null,
                identityId
        );

        StaffMemberHiredEvent event = (StaffMemberHiredEvent)
                staffMember.listOfDomainEvents().getFirst();

        assertThat(event.staffId()).isEqualTo(staffMember.getStaffId());
        assertThat(event.identityId()).isEqualTo(identityId);
        assertThat(staffMember.getIdentityId()).isEqualTo(identityId);
    }
}
