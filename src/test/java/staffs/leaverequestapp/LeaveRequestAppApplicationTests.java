package staffs.leaverequestapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import staffs.leaverequestapp.identity.authService.FirebaseAuthService;

@SpringBootTest
class LeaveRequestAppApplicationTests {

    @MockitoBean
    private FirebaseAuthService firebaseAuthService;

    @Test
    void contextLoads() {
    }

}
