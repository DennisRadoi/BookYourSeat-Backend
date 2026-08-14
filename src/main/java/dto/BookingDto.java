//package dto;
//
//import entities.Booking;
//
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.time.OffsetDateTime;
//import entities.enums.BookingStatus;
//
//public record BookingDTO(
//        Integer id,
//        Integer userId,
//        Integer roomId,
//        Integer seatId,
//        Integer recurringBookingId,
//        LocalTime startTime,
//        LocalTime endTime,
//        BookingStatus status,
//        LocalDate date,
//        LocalDate endDate,
//        OffsetDateTime createdAt,
//        OffsetDateTime updatedAt
//) {
//    public static BookingDTO fromEntity(Booking booking) {
//        if (booking == null) {
//            return null;
//        }
//
//        return new BookingDTO(
//                booking.getId(),
//                booking.getUserId(),
//                booking.getRoomId(),
//                booking.getSeat() != null ? booking.getSeat().getId() : null,
//                booking.getRecurringBooking() != null ? booking.getRecurringBooking().getId() : null,
//                booking.getStartTime(),
//                booking.getEndTime(),
//                booking.getStatus(),
//                booking.getDate(),
//                booking.getEndDate(),
//                booking.getCreatedAt(),
//                booking.getUpdatedAt()
//        );
//    }
//}
