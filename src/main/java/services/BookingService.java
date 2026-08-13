package services;

import entities.Booking;
import entities.RecurringBooking;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repositories.BookingRepository;
import repositories.RecurringBookingRepository;

import org.springframework.transaction.annotation.Transactional;
//import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RecurringBookingRepository recurringBookingRepository;

    public List<Booking> getUserBookings(Integer userId, String status) {
        if (status == null || status.isBlank()) {
            return bookingRepository.findByUserId(userId);
        }

        try {
            entities.BookingStatus bs = entities.BookingStatus.valueOf(status);
            return bookingRepository.findByUserId(userId).stream()
                    .filter(b -> b.getStatus() == bs)
                    .toList();
        } catch (IllegalArgumentException ex) {

            return bookingRepository.findByUserId(userId).stream()
                    .filter(b -> b.getStatus() != null && b.getStatus().name().equalsIgnoreCase(status))
                    .toList();
        }
    }

    public Booking getBookingById(Integer id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + id));
    }

    public Booking createBooking(Booking booking) {

        return bookingRepository.save(booking);
    }

    public Booking updateBooking(Integer id, Booking booking) {
        Booking existing = getBookingById(id);

        existing.setStartTime(booking.getStartTime());
        existing.setEndTime(booking.getEndTime());
        existing.setStatus(booking.getStatus());
        existing.setDate(booking.getDate());
        existing.setEndDate(booking.getEndDate());
        existing.setSeat(booking.getSeat());
        return bookingRepository.save(existing);
    }

    public Map<String, String> cancelBooking(Integer id) {
        Booking booking = getBookingById(id);
        booking.setStatus(entities.BookingStatus.anulata);
        bookingRepository.save(booking);
        Map<String, String> resp = new HashMap<>();
        resp.put("status", "ok");
        return resp;
    }

    public RecurringBooking updateRecurringBooking(Integer id, RecurringBooking recurringBooking) {
        RecurringBooking existing = recurringBookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recurring booking not found: " + id));
        existing.setFrequency(recurringBooking.getFrequency());
        existing.setDaysOfWeek(recurringBooking.getDaysOfWeek());
        existing.setIntervalOfReccur(recurringBooking.getIntervalOfReccur());
        return recurringBookingRepository.save(existing);
    }

    public Map<String, String> cancelRecurringBookingSeries(Integer id) {
        List<Booking> series = bookingRepository.findByRecurringBookingId(id);
        for (Booking b : series) {
            b.setStatus(entities.BookingStatus.anulata);
        }
        bookingRepository.saveAll(series);
        Map<String, String> resp = new HashMap<>();
        resp.put("status", "ok");
        return resp;
    }
}
