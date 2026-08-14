package dto;

public record UpdateRecurringBookingRequest(
        String frequency,
        String daysOfWeek,
        Integer intervalOfRecurrence
) {
}
