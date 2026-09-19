package staffs.leaverequestapp.leave.ui;

import java.time.LocalDate;
public record SubmitLeaveRequestCommand(
        String identityId,
        LocalDate startDate,
        LocalDate endDate,
        String reason)
{
//validation
}
