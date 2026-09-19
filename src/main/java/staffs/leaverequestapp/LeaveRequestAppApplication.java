package staffs.leaverequestapp;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.retry.annotation.EnableRetry;

@EnableRabbit
@SpringBootApplication
@EnableAsync      // Turns on the @Async background thread
@EnableRetry
public class LeaveRequestAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeaveRequestAppApplication.class, args);
    }

}
