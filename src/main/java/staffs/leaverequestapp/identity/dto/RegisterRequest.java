package staffs.leaverequestapp.identity.dto;

import staffs.leaverequestapp.identity.security.Role;

public record RegisterRequest(
    String email,
    String password,
    String firstName,
    String surname,
    Role role
) {
    // add validation
}
