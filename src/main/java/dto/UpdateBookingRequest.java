package dto;

import entities.enums.BookingStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public record UpdateBookingRequest(
        Integer roomId,
        Integer seatId,
        LocalDate startDate,
        LocalDate endDate,
        LocalTime startTime,
        LocalTime endTime,
        BookingStatus status
) {
}
