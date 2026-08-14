package dto;

import entities.Booking;
import entities.enums.BookingStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;

public record GetBookingResponse(
        Integer id,
        Integer userId,
        Integer roomId,
        Integer seatId,
        Integer recurringBookingId,
        LocalDate startDate,
        LocalDate endDate,
        LocalTime startTime,
        LocalTime endTime,
        BookingStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static GetBookingResponse fromEntity(Booking booking) {
        if (booking == null) {
            return null;
        }

        return new GetBookingResponse(
                booking.getId(),
                booking.getUser() != null ? booking.getUser().getId() : null,
                booking.getRoom() != null ? booking.getRoom().getId() : null,
                booking.getSeat() != null ? booking.getSeat().getId() : null,
                booking.getRecurringBooking() != null ? booking.getRecurringBooking().getId() : null,
                booking.getStartDate(),
                booking.getEndDate(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getStatus(),
                booking.getCreatedAt(),
                booking.getUpdatedAt()
        );
    }
}
