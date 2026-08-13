package dto;

import entities.Booking;

// only seat bookings
public record BookingDTOV2(String dateOfBooking, String startTime, String endTime,
                         Integer floor, String building) {
    public static BookingDTOV2 fromEntity(Booking booking) {
        return new BookingDTOV2(
                String.valueOf(booking.getStartDate()),
                String.valueOf(booking.getStartTime()),
                String.valueOf(booking.getEndTime()),
                booking.getSeat().getRoom().getFloor(),
                booking.getSeat().getRoom().getBuilding().getName()
        );
    }
}