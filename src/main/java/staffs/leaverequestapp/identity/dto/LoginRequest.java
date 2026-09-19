package staffs.leaverequestapp.identity.dto;

public record LoginRequest(
        String email,
        String password
) {
    // add validation
}
