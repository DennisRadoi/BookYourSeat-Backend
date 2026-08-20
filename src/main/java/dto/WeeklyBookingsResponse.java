package dto;

import java.time.LocalDate;
import java.util.List;

public record WeeklyBookingsResponse(
        LocalDate weekStart,
        LocalDate weekEnd,
        List<WeeklyDayBookingsResponse> days
) {
}