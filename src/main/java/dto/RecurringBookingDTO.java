package dto;

import entities.RecurringBooking;

public record RecurringBookingDTO(
        Integer id,
        String frequency,
        String daysOfWeek,
        String intervalOfReccur
) {
    public static RecurringBookingDTO fromEntity(RecurringBooking recurringBooking) {
        if (recurringBooking == null) {
            return null;
        }

        return new RecurringBookingDTO(
                recurringBooking.getId(),
                recurringBooking.getFrequency(),
                recurringBooking.getDaysOfWeek(),
                recurringBooking.getIntervalOfReccur()
        );
    }
}
