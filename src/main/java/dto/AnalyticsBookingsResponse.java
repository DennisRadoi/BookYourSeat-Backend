package dto;

public record AnalyticsBookingsResponse(
        int year,
        int month,
        long totalBookings) {

}