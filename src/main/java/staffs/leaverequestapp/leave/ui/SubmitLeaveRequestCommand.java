package staffs.leaverequestapp.leave.ui;

import java.time.LocalDate;
import java.util.UUID;

public record SubmitLeaveRequestCommand(
        UUID staffId,
        LocalDate startDate,
        LocalDate endDate,
        String reason)
{
//validation
}
