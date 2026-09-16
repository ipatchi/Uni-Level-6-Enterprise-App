package staffs.leaverequestapp.common.domain;

public final class DomainAssertions {
    private DomainAssertions() {}

    public static void argumentLength(String value, int min, int max, String errorMessage) {
        int length = value.trim().length();
        if (length < min || length > max) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static String argumentNotEmpty(String value, String errorMessage) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(errorMessage);
        }
        return value.trim();
    }
}