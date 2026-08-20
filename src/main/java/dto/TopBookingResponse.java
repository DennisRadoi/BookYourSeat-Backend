package dto;

public record TopBookingResponse(
        String employeeName,
        long bookingCount,
        double percentage
) {
}
