package staffs.leaverequestapp.identity;

import com.google.firebase.auth.UserRecord;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import staffs.leaverequestapp.identity.authService.FirebaseAuthService;
import staffs.leaverequestapp.identity.dto.LoginRequest;
import staffs.leaverequestapp.identity.dto.LoginResponse;
import staffs.leaverequestapp.identity.dto.RegisterRequest;
import staffs.leaverequestapp.identity.dto.RegisterResponse;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
@Slf4j
public class AuthController {
    public final String USER_CREATED_CONFIRMATION = "User created successfully";
    private final FirebaseAuthService firebaseAuthService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) throws Exception{
        log.info("Registering user {}", request);
        UserRecord userRecord = firebaseAuthService.registerUser(
                request.firstName(),
                request.surname(),
                request.email(),
                request.password(),
                request.role()
        );

        RegisterResponse response = new RegisterResponse(userRecord.getUid(),
                userRecord.getEmail(),
                userRecord.getDisplayName(),
                USER_CREATED_CONFIRMATION
        );


        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request){
        LoginResponse response = firebaseAuthService.loginUser(request.email(),
                request.password()
        );
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/role-check")
    public ResponseEntity<String> roleCheck(Authentication authentication) {
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(", "));
        return ResponseEntity.ok(roles +" access granted");
    }
}
