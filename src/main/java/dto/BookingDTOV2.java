package dto;

import entities.Booking;
import entities.Room;

// Rezervarea poate fi pentru un loc sau pentru întreaga sală.
public record BookingDTOV2(String dateOfBooking, String startTime, String endTime,
                         Integer floor, String building) {
    public static BookingDTOV2 fromEntity(Booking booking) {
        Room room = booking.getSeat() != null
                ? booking.getSeat().getRoom()
                : booking.getRoom();

        return new BookingDTOV2(
                String.valueOf(booking.getStartDate()),
                String.valueOf(booking.getStartTime()),
                String.valueOf(booking.getEndTime()),
                room == null ? null : room.getFloor(),
                room == null ? null : room.getBuilding().getName()
        );
    }
}
