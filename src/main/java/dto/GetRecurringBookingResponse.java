package dto;

import entities.RecurringBooking;

public record GetRecurringBookingResponse(
        Integer id,
        Integer bookingId,
        String frequency,
        String daysOfWeek,
        Integer intervalOfRecurrence
) {
    public static GetRecurringBookingResponse fromEntity(RecurringBooking recurringBooking) {
        if (recurringBooking == null) {
            return null;
        }

        return new GetRecurringBookingResponse(
                recurringBooking.getId(),
                recurringBooking.getBooking() != null ? recurringBooking.getBooking().getId() : null,
                recurringBooking.getFrequency(),
                recurringBooking.getDaysOfWeek(),
                recurringBooking.getIntervalOfRecurrence()
        );
    }
}
