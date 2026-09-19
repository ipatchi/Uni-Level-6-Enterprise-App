package staffs.leaverequestapp.identity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginResponse(
        @JsonProperty("localId") String uid,
        String email,
        @JsonProperty("idToken") String accessToken,
        String refreshToken,
        @JsonProperty("expiresIn")  String expiresInSeconds

) {
    // add validation
}

