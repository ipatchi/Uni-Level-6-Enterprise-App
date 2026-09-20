package staffs.leaverequestapp.identity.authService;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import staffs.leaverequestapp.common.events.UserRegisteredEvent;
import staffs.leaverequestapp.common.events.infra.DomainEventManager;
import staffs.leaverequestapp.identity.dto.LoginResponse;
import staffs.leaverequestapp.identity.security.Role;

import java.util.List;
import java.util.Map;
import java.util.UUID;


@Service
@Slf4j
public class FirebaseAuthService {
    private final FirebaseAuth firebaseAuth;
    private final RestClient restClient;
    private final DomainEventManager domainEventManager;

    @Value("${firebase.web-api-key}")
    private String firebaseApiKey;

    public FirebaseAuthService(FirebaseAuth firebaseAuth, ApplicationEventPublisher eventPublisher, DomainEventManager domainEventManager) {
        this.firebaseAuth = firebaseAuth;
        this.restClient = RestClient.create();
        this.domainEventManager = domainEventManager;
    }

    @Transactional()
    public UserRecord registerUser(String firstName,
                                   String surname,
                                   String email,
                                   String password,
                                   Role role) throws Exception {
        CreateRequest createRequest = new CreateRequest().setEmail(email)
                .setPassword(password)
                .setDisplayName(firstName + " " + surname)
                .setEmailVerified(false);

        UserRecord userRecord = firebaseAuth.createUser(createRequest);

        Role confirmedRole = role != null ? role : Role.STAFF;

        Map<String, Object> customClaims = Map.of(
                "role", confirmedRole.name(),
                "admin", confirmedRole == Role.ADMIN
        );


        UserRegisteredEvent event = new UserRegisteredEvent(
                userRecord.getUid(),
                email,
                firstName,
                surname,
                confirmedRole.name()
        );

        domainEventManager.manageDomainEvents("IdentityContext", List.of(event));
        log.info("Published UserRegisteredEvent for email: {}", email);

        firebaseAuth.setCustomUserClaims(userRecord.getUid(), customClaims);
        return userRecord;
    }

    public LoginResponse loginUser(String email,
                                   String password) {
        if (email == null || email.isBlank()
                || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Email and password must not be empty");
        }

        Map<String, Object> requestBody = Map.of(
                "email", email,
                "password", password,
                "returnSecureToken", true
        );

        String firebaseLoginUrl = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + firebaseApiKey;

        try {
            return restClient.post()
                    .uri(firebaseLoginUrl)
                    .body(requestBody)
                    .retrieve()
                    .body(LoginResponse.class);
        } catch (RestClientResponseException e) {
            log.error("Firebase Auth error [{}] {}",
                    e.getStatusCode(),
                    e.getResponseBodyAsString());
            throw new IllegalArgumentException("Invalid email or password");
        }
    }
}
