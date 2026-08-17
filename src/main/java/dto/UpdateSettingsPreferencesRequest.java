package dto;

public record UpdateSettingsPreferencesRequest(String preferredBuilding,
                                               String daysOfWeek,
                                               String preferredStartTime,
                                               String preferredEndTime,
                                               Boolean receivesNotificationOnEmail,
                                               Boolean reminderBeforeBooking) {

}
