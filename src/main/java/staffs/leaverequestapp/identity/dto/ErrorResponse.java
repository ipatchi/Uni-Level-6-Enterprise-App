package staffs.leaverequestapp.identity.dto;

public record ErrorResponse(
        String error,
        String message
) {
    public ErrorResponse(String error) {
        this(error, null);
    }
}
