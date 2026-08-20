package dto;

public record TopBookingResponse(
        String employeeName,
        long seatCount,
        double occupancyPercentage
) {
}
