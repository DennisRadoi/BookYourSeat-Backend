package dto;

import entities.User;
import entities.UserPreferences;

public record MySettingsResponse(boolean quietPlace, boolean nearWindow,
                                 String daysOfWeek,
                                 String preferredStartTime,
                                 String preferredEndTime,
                                 boolean reminderBeforeBooking,
                                 boolean bookingConfirmationOnEmail,
                                 String preferredBuilding,
                                 boolean isActive) {
    public static MySettingsResponse fromEntity(User user) {
        UserPreferences preferinte = user.getUserPreferences();
        return new MySettingsResponse(
                preferinte == null ? null : preferinte.getQuietPlace(),
                preferinte == null ? null : preferinte.getNearWindow(),
                preferinte == null ? null : preferinte.getDaysOfWeek(),
                preferinte == null || preferinte.getPreferredStartTime() == null
                        ? null : preferinte.getPreferredStartTime().toString(),
                preferinte == null || preferinte.getPreferredEndTime() == null
                        ? null : preferinte.getPreferredEndTime().toString(),
                preferinte == null ? null : preferinte.getReminderBeforeBooking(),
                preferinte == null ? null : preferinte.getBookingConfirmationOnEmail(),
                preferinte == null || preferinte.getPreferredBuilding() == null
                        ? null : preferinte.getPreferredBuilding().getName()
                , user.getIsActive()
        );
    }
}
