package staffs.leaverequestapp.identity.authService;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Configuration
@Slf4j
public class FirebaseConfig {
    public final String FIREBASE_CREDENTIALS_FILE_MISSING = "Firebase credentials file missing";
    public final String SERVICE_ACCOUNT_DOES_NOT_CONTAIN_VALID_PROJECT_ID = "Make sure your service account JSON contains a valid project_id";

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.getInstance();
        }

        final String RESOURCE_FILE = "serviceAccountKey.json";
        ClassPathResource resource = new ClassPathResource(RESOURCE_FILE);

        if (!resource.exists()) {
            throw new FileNotFoundException(FIREBASE_CREDENTIALS_FILE_MISSING);
        }

        try (InputStream serviceAccount = resource.getInputStream()) {
            GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
            String projectId = null;

            if (credentials instanceof ServiceAccountCredentials sac) {
                projectId = sac.getProjectId();
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .setProjectId(projectId)
                    .build();

            return FirebaseApp.initializeApp(options);
        }
    }

    @Bean
    public FirebaseAuth firebaseAuth(FirebaseApp firebaseApp) {
        return FirebaseAuth.getInstance(firebaseApp);
    }

    @Bean
    public JwtDecoder jwtDecoder(FirebaseApp firebaseApp) {
        String projectId = firebaseApp.getOptions().getProjectId();
        if (projectId == null || projectId.isBlank()) {
            throw new IllegalStateException(SERVICE_ACCOUNT_DOES_NOT_CONTAIN_VALID_PROJECT_ID);
        }

        String jwkSetUri = "https://www.googleapis.com/service_accounts/v1/jwk/securetoken@system.gserviceaccount.com";
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();

        String issuerUri = "https://securetoken.google.com/" + projectId;

        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuerUri);

        OAuth2TokenValidator<Jwt> withAudience = new JwtClaimValidator<List<String>>(
                "aud", audList -> audList != null && audList.contains(projectId)
        );


        OAuth2TokenValidator<Jwt> combinedValidator =
                new DelegatingOAuth2TokenValidator<>(withIssuer, withAudience);

        jwtDecoder.setJwtValidator(combinedValidator);
        return jwtDecoder;
    }
}

