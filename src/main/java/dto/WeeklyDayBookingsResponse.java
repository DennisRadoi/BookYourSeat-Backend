package dto;

public record WeeklyDayBookingsResponse(
        String day,
        long officeBookings,
        long conferenceRoomBookings
) {
}