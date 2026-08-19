package controllers;

import services.BookingService;
import services.UserService;
import dto.CreateBookingRequest;
import dto.UpdateBookingRequest;
import dto.UpdateRecurringBookingRequest;
import dto.GetBookingResponse;
import dto.GetRecurringBookingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;

    @GetMapping("/bookings/me")
    public ResponseEntity<List<GetBookingResponse>> getMyBookings(
            @RequestParam(name = "status", required = false) String status) {
        Integer userId = userService.getCurrentUser().getId();
        return ResponseEntity.ok(bookingService.getUserBookingsDTO(userId, status));
    }

    @GetMapping("/bookings/{id}")
    public ResponseEntity<GetBookingResponse> getBookingById(@PathVariable Integer id) {
        return ResponseEntity.ok(bookingService.getBookingByIdDTO(id));
    }

    @PostMapping("/bookings")
    public ResponseEntity<GetBookingResponse> createBooking(@RequestBody CreateBookingRequest request) {
        return new ResponseEntity<>(bookingService.createBookingDTO(request), HttpStatus.CREATED);
    }

    @PutMapping("/bookings/{id}")
    public ResponseEntity<GetBookingResponse> updateBooking(
            @PathVariable Integer id,
            @RequestBody UpdateBookingRequest request) {
        return ResponseEntity.ok(bookingService.updateBookingDTO(id, request));
    }

    @PutMapping("/bookings/{id}/cancel")
    public ResponseEntity<Map<String, String>> cancelBooking(@PathVariable Integer id) {
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }

    @GetMapping("/recurring-bookings/{id}")
    public ResponseEntity<GetRecurringBookingResponse> getRecurringBookingById(@PathVariable Integer id) {
        return ResponseEntity.ok(bookingService.getRecurringBookingByIdDTO(id));
    }

    @PutMapping("/recurring-bookings/{id}")
    public ResponseEntity<GetRecurringBookingResponse> updateRecurringBooking(
            @PathVariable Integer id,
            @RequestBody UpdateRecurringBookingRequest request) {
        return ResponseEntity.ok(bookingService.updateRecurringBookingDTO(id, request));
    }

    @PatchMapping("/recurring-bookings/{id}")
    public ResponseEntity<Map<String, String>> cancelRecurringBookingSeries(@PathVariable Integer id) {
        return ResponseEntity.ok(bookingService.cancelRecurringBookingSeries(id));
    }
}
