package dto;

import entities.User;
import entities.UserPreferences;

public record MySettingsResponse(String preferedFloor,
                                 String daysOfWeek,
                                 String preferredStartTime,
                                 String preferredEndTime,
                                 boolean reminderBeforeBooking,
                                 boolean bookingConfirmationOnEmail) {
    public static MySettingsResponse fromEntity(User user) {
        UserPreferences preferinte = user.getUserPreferences();
        return new MySettingsResponse(
                preferinte == null ? null : preferinte.getPreferredBuilding().getName(),
                preferinte == null ? null : preferinte.getDaysOfWeek(),
                preferinte == null ? null : String.valueOf(preferinte.getPreferredStartTime()),
                preferinte == null ? null : String.valueOf(preferinte.getPreferredEndTime()),
                preferinte == null ? null : preferinte.getReminderBeforeBooking(),
                preferinte == null ? null : preferinte.getBookingConfirmationOnEmail()
        );
    }
}
