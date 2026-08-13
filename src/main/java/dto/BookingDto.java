package dto;

import entities.Booking;

// only seat bookings
public record BookingDto(String dateOfBooking, String startTime, String endTime,
                         Integer floor, String building) {
    public static BookingDto fromEntity(Booking booking) {
        return new BookingDto(
                String.valueOf(booking.getStartDate()),
                String.valueOf(booking.getStartTime()),
                String.valueOf(booking.getEndTime()),
                booking.getSeat().getRoom().getFloor(),
                booking.getSeat().getRoom().getBuilding().getName()
        );
    }
}
