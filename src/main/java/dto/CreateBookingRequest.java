package dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreateBookingRequest(
        Integer userId,
        Integer roomId,
        Integer seatId,
        LocalDate startDate,
        LocalDate endDate,
        LocalTime startTime,
        LocalTime endTime,

        String recurrenceFrequency,
        String recurrenceDaysOfWeek,
        Integer recurrenceIntervalOfRecurrence
) {
    public boolean isRecurring() {
        return recurrenceFrequency != null && !recurrenceFrequency.isBlank();
    }
}
