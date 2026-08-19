package services;

import dto.GetBookingResponse;
import dto.CreateBookingRequest;
import dto.GetRecurringBookingResponse;
import dto.UpdateBookingRequest;
import dto.UpdateRecurringBookingRequest;
import entities.Booking;
import entities.RecurringBooking;
import entities.Room;
import entities.Seat;
import entities.User;
import entities.enums.BookingStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repositories.BookingRepository;
import repositories.RecurringBookingRepository;
import repositories.RoomRepository;
import repositories.SeatRepository;
import repositories.UserRepository;

import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingService {

    // DTO imports moved inside service to map within transactional boundary
    

    private final BookingRepository bookingRepository;
    private final RecurringBookingRepository recurringBookingRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final SeatRepository seatRepository;
    private final EmailService emailService;

    // DTO mapping methods — perform mapping inside transactional service to avoid LazyInitializationException
    public java.util.List<GetBookingResponse> getUserBookingsDTO(Integer userId, String status) {
        return getUserBookings(userId, status).stream()
                .map(GetBookingResponse::fromEntity)
                .toList();
    }

    public GetBookingResponse getBookingByIdDTO(Integer id) {
        return GetBookingResponse.fromEntity(getBookingById(id));
    }

    public GetBookingResponse createBookingDTO(CreateBookingRequest request, Integer currentUserId) {
        Booking created = createBooking(request, currentUserId);
        return GetBookingResponse.fromEntity(created);
    }

    public GetBookingResponse updateBookingDTO(Integer id, UpdateBookingRequest request, Integer currentUserId) {
        Booking updated = updateBooking(id, request, currentUserId);
        return GetBookingResponse.fromEntity(updated);
    }

    public GetRecurringBookingResponse getRecurringBookingByIdDTO(Integer id) {
        return GetRecurringBookingResponse.fromEntity(getRecurringBookingById(id));
    }

    public GetRecurringBookingResponse updateRecurringBookingDTO(Integer id, UpdateRecurringBookingRequest request, Integer currentUserId) {
        RecurringBooking updated = updateRecurringBooking(id, request, currentUserId);
        return GetRecurringBookingResponse.fromEntity(updated);
    }

    public List<Booking> getUserBookings(Integer userId, String status) {
        if (status == null || status.isBlank()) {
            return bookingRepository.findByUserId(userId);
        }

        try {
            BookingStatus bs = BookingStatus.valueOf(status.toUpperCase());
            return bookingRepository.findByUserIdAndStatus(userId, bs);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Status necunoscut pentru rezervare: " + status);
        }
    }

    public Booking getBookingById(Integer id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + id));
    }

    public Booking createBooking(CreateBookingRequest request, Integer currentUserId) {
//        if (request.userId() == null) {
//            throw new IllegalArgumentException("userId este obligatoriu");
//        }

        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + currentUserId));

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setStartDate(request.startDate());
        booking.setEndDate(request.endDate());
        booking.setStartTime(request.startTime());
        booking.setEndTime(request.endTime());

        if (request.roomId() != null) {
            Room room = roomRepository.findById(request.roomId())
                    .orElseThrow(() -> new IllegalArgumentException("Room not found: " + request.roomId()));
            booking.setRoom(room);
        }
        if (request.seatId() != null) {
            Seat seat = seatRepository.findById(request.seatId())
                    .orElseThrow(() -> new IllegalArgumentException("Seat not found: " + request.seatId()));
            booking.setSeat(seat);
        }

        validateBookingInterval(booking);
        if (booking.getSeat() != null) {
            checkSeatAvailability(booking, null);
        }

        if (request.isRecurring()) {
            RecurringBooking recurringBooking = new RecurringBooking();
            recurringBooking.setFrequency(request.recurrenceFrequency());
            recurringBooking.setDaysOfWeek(request.recurrenceDaysOfWeek());
            recurringBooking.setIntervalOfRecurrence(request.recurrenceIntervalOfRecurrence());
            // relatie 1-la-1 cu @MapsId: cheia primara a RecurringBooking e derivata din Booking,
            // deci legatura inapoi trebuie setata explicit inainte de save
            recurringBooking.setBooking(booking);
            booking.setRecurringBooking(recurringBooking);
        }

        Map<String, Object> vars = new HashMap<>();
        vars.put("userName", user.getFirstName());
        vars.put("startDate", booking.getStartDate().toString());
        vars.put("startTime", booking.getStartTime().toString());
        vars.put("location", booking.getSeat() != null ? "Locul #" + booking.getSeat().getId() : booking.getRoom().getName());

        emailService.sendEmail(user.getEmail(), "Confirmare Rezervare Birou", "booking-confirmation", vars);

        return bookingRepository.save(booking);
    }

    public Booking updateBooking(Integer id, UpdateBookingRequest request, Integer currentUserId) {
        Booking existing = getBookingById(id);

        if (existing.getUser() == null || !existing.getUser().getId().equals(currentUserId)) {
            throw new IllegalArgumentException("User not allowed to modify booking: " + currentUserId);
        }

        existing.setStartDate(request.startDate());
        existing.setEndDate(request.endDate());
        existing.setStartTime(request.startTime());
        existing.setEndTime(request.endTime());

        existing.setRoom(request.roomId() != null
                ? roomRepository.findById(request.roomId())
                        .orElseThrow(() -> new IllegalArgumentException("Room not found: " + request.roomId()))
                : null);
        existing.setSeat(request.seatId() != null
                ? seatRepository.findById(request.seatId())
                        .orElseThrow(() -> new IllegalArgumentException("Seat not found: " + request.seatId()))
                : null);

        if (request.status() != null) {
            existing.setStatus(request.status());
        }

        validateBookingInterval(existing);
        if (existing.getSeat() != null) {
            checkSeatAvailability(existing, existing.getId());
        }

        Map<String, Object> updateVars = new HashMap<>();
        updateVars.put("userName", existing.getUser().getFirstName());
        updateVars.put("startDate", existing.getStartDate().toString());
        updateVars.put("startTime", existing.getStartTime().toString());
        updateVars.put("location", existing.getSeat() != null ? "Locul #" + existing.getSeat().getId() : existing.getRoom().getName());

        emailService.sendEmail(existing.getUser().getEmail(), "Modificare Rezervare Birou", "booking-update", updateVars);

        return bookingRepository.save(existing);
    }

    public Map<String, String> cancelBooking(Integer id, Integer currentUserId) {
        Booking booking = getBookingById(id);
        if (booking.getUser() == null || !booking.getUser().getId().equals(currentUserId)) {
            throw new IllegalArgumentException("User not allowed to cancel booking: " + currentUserId);
        }
        booking.setStatus(BookingStatus.ANULATA);
        bookingRepository.save(booking);
        Map<String, String> resp = new HashMap<>();
        resp.put("status", "ok");

        Map<String, Object> cancelVars = new HashMap<>();
        cancelVars.put("userName", booking.getUser().getFirstName());
        cancelVars.put("startDate", booking.getStartDate().toString());
        cancelVars.put("location", booking.getSeat() != null ? "Locul #" + booking.getSeat().getId() : booking.getRoom().getName());

        emailService.sendEmail(booking.getUser().getEmail(), "Anulare Rezervare Birou", "booking-cancellation", cancelVars);

        return resp;
    }

    public RecurringBooking getRecurringBookingById(Integer id) {
        return recurringBookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recurring booking not found: " + id));
    }

    public RecurringBooking updateRecurringBooking(Integer id, UpdateRecurringBookingRequest request, Integer currentUserId) {
        RecurringBooking existing = getRecurringBookingById(id);
        if (existing.getBooking() == null || existing.getBooking().getUser() == null || !existing.getBooking().getUser().getId().equals(currentUserId)) {
            throw new IllegalArgumentException("User not allowed to modify recurring booking: " + currentUserId);
        }
        existing.setFrequency(request.frequency());
        existing.setDaysOfWeek(request.daysOfWeek());
        existing.setIntervalOfRecurrence(request.intervalOfRecurrence());
        return recurringBookingRepository.save(existing);
    }

    // in modelul actual, o "serie" recurenta = un singur Booking legat 1-1 de RecurringBooking
    // (aceeasi cheie primara), asa ca anularea seriei inseamna anularea acelui booking
    public Map<String, String> cancelRecurringBookingSeries(Integer id, Integer currentUserId) {
        List<Booking> series = bookingRepository.findByRecurringBookingId(id);
        for (Booking b : series) {
            if (b.getUser() == null || !b.getUser().getId().equals(currentUserId)) {
                throw new IllegalArgumentException("User not allowed to cancel recurring series: " + currentUserId);
            }
            b.setStatus(BookingStatus.ANULATA);
        }
        bookingRepository.saveAll(series);
        Map<String, String> resp = new HashMap<>();
        resp.put("status", "ok");
        return resp;
    }

    private void validateBookingInterval(Booking booking) {
        if (booking.getStartDate() == null || booking.getEndDate() == null
                || booking.getStartTime() == null || booking.getEndTime() == null) {
            throw new IllegalArgumentException("Data si ora de inceput/sfarsit sunt obligatorii");
        }
        if (booking.getEndDate().isBefore(booking.getStartDate())) {
            throw new IllegalArgumentException("Data de sfarsit nu poate fi inainte de data de inceput");
        }
        if (booking.getStartDate().equals(booking.getEndDate())
                && !booking.getStartTime().isBefore(booking.getEndTime())) {
            throw new IllegalArgumentException("Ora de inceput trebuie sa fie inainte de ora de sfarsit");
        }
        if ((booking.getRoom() == null) == (booking.getSeat() == null)) {
            throw new IllegalArgumentException("Rezervarea trebuie sa aiba fie o sala (room), fie un loc (seat), dar nu ambele");
        }
    }

    private void checkSeatAvailability(Booking booking, Integer excludeBookingId) {
        boolean overlaps = bookingRepository.existsOverlappingSeatBooking(
                booking.getSeat().getId(),
                booking.getStartDate(),
                booking.getEndDate(),
                booking.getStartTime(),
                booking.getEndTime(),
                excludeBookingId
        );
        if (overlaps) {
            throw new IllegalStateException("Locul selectat este deja rezervat in intervalul ales");
        }
    }
}
