package staffs.leaverequestapp;

import org.springframework.boot.SpringApplication;

public class TestLeaveRequestAppApplication {

    public static void main(String[] args) {
        SpringApplication.from(LeaveRequestAppApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
