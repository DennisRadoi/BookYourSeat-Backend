package dto;

public record TodayAnalyticsResponse(
        long conferenceRoomsOccupancyPercent,
        long officeOccupancyPercent,
        long peopleInOffice
) {
}