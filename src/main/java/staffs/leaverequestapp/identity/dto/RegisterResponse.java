package staffs.leaverequestapp.identity.dto;


public record RegisterResponse(
        String uid,
        String email,
        String displayName,
        String message
) {
    // add validation
}
