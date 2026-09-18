package staffs.leaverequestapp.staff;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import staffs.leaverequestapp.common.domain.FullName;
import staffs.leaverequestapp.common.domain.Identity;
import staffs.leaverequestapp.staff.domain.*;
import staffs.leaverequestapp.staff.persistance.entities.StaffJpa;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Staff Aggregate Tests")
class StaffAggregateTests {
    Identity<StaffJpa> validId = Identity.generateId();
    private final FullName validName = new FullName("Test", "User");
    private final Organisation validOrg = new Organisation(LocalDate.now(), "Testing", null);
    private final Placement validPlacement = new Placement("Tester", LocalDate.now(), "Testing Level", EmploymentType.FULL_TIME);

    @Nested
    @DisplayName("Staff Aggregate Tests")
    class CoreAggregateRules {

        @Test
        @DisplayName("Successfully hiring a staff member enforces the ACTIVE status")
        void validHire() {
            StaffMember newStaff = StaffMember.hire(validId, validName, "test.user@email.com", validOrg, validPlacement);
            assertNotNull(newStaff);
            assertInstanceOf(UUID.class, newStaff.getStaffId(), "The generated ID must be a valid UUID");
            assertEquals("test.user@email.com", newStaff.getEmail());
            assertEquals(EmploymentStatus.ACTIVE, newStaff.getEmploymentStatus(), "Rule: All new hires must default to ACTIVE");
        }

        @Test
        @DisplayName("Cannot create user if email address does not contain an @ symbol")
        void invalidEmailFormat() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    StaffMember.hire(validId, validName, "testuseremail.com", validOrg, validPlacement)
            );

            assertEquals("A valid email address is required", exception.getMessage());
        }

        @Test
        @DisplayName("Cannot create user if email address is null")
        void nullEmail() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    StaffMember.hire(validId, validName, null, validOrg, validPlacement)
            );

            assertEquals("A valid email address is required", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Organisation Aggregate Rule Tests")
    class OrganisationRules {

        @Test
        @DisplayName("Organisation creation fails if hire date is missing")
        void missingHireDate() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    new Organisation(null, "Testing", null)
            );

            assertEquals("Hire date cannot be null", exception.getMessage());
        }

        @Test
        @DisplayName("Organisation creation fails if department is blank")
        void blankDepartment() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    new Organisation(LocalDate.now(), "   ", null)
            );

            assertEquals("Department cannot be blank", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Placement Value Object Rules")
    class PlacementRules {

        @Test
        @DisplayName("Placement creation fails if current role is missing")
        void missingRole() {
            NullPointerException exception = assertThrows(NullPointerException.class, () ->
                    new Placement(null, LocalDate.now(), "Testing Level", EmploymentType.FULL_TIME)
            );

            assertEquals("Current role cannot be null", exception.getMessage());
        }

        @Test
        @DisplayName("Placement creation fails if employment type is missing")
        void missingEmploymentType() {
            NullPointerException exception = assertThrows(NullPointerException.class, () ->
                    new Placement("Tester", LocalDate.now(), "Testing Level", null)
            );

            assertEquals("Employment type cannot be null", exception.getMessage());
        }
    }
}