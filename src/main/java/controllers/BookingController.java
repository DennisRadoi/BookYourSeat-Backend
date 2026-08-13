package controllers;


import services.BookingService;
import dto.BookingDTO;
import dto.RecurringBookingDTO;
import entities.Booking;
import entities.RecurringBooking;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/bookings/me")
    public ResponseEntity<List<BookingDTO>> getMyBookings(
            @RequestParam(name = "userId", defaultValue = "1") Integer userId,
            @RequestParam(name = "status", required = false) String status) {
        return ResponseEntity.ok(bookingService.getUserBookings(userId, status).stream()
                .map(BookingDTO::fromEntity)
                .collect(Collectors.toList()));
    }

    @GetMapping("/bookings/{id}")
    public ResponseEntity<BookingDTO> getBookingById(@PathVariable Integer id) {
        return ResponseEntity.ok(BookingDTO.fromEntity(bookingService.getBookingById(id)));
    }

    @PostMapping("/bookings")
    public ResponseEntity<BookingDTO> createBooking(@RequestBody Booking booking) {
        Booking created = bookingService.createBooking(booking);
        return new ResponseEntity<>(BookingDTO.fromEntity(created), HttpStatus.CREATED);
    }

    @PutMapping("/bookings/{id}")
    public ResponseEntity<BookingDTO> updateBooking(@PathVariable Integer id, @RequestBody Booking booking) {
        return ResponseEntity.ok(BookingDTO.fromEntity(bookingService.updateBooking(id, booking)));
    }

    @PutMapping("/bookings/{id}/cancel")
    public ResponseEntity<Map<String, String>> cancelBooking(@PathVariable Integer id) {
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }

    @PutMapping("/recurring-bookings/{id}")
    public ResponseEntity<RecurringBookingDTO> updateRecurringBooking(
            @PathVariable Integer id,
            @RequestBody RecurringBooking recurringBooking) {
        return ResponseEntity.ok(RecurringBookingDTO.fromEntity(bookingService.updateRecurringBooking(id, recurringBooking)));
    }

    @PatchMapping("/recurring-bookings/{id}")
    public ResponseEntity<Map<String, String>> cancelRecurringBookingSeries(@PathVariable Integer id) {
        return ResponseEntity.ok(bookingService.cancelRecurringBookingSeries(id));
    }
}