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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
        // Recurența este materializată în rezervări independente. Astfel,
        // fiecare apariție are propriul conflict, status și loc în calendar.
        if (request.isRecurring()) {
            return createRecurringBookings(request, currentUserId);
        }
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
        if (booking.getRoom() != null && booking.getSeat() == null) {
            if (bookingRepository.existsOverlappingBookingInRoom(
                    booking.getRoom().getId(), booking.getStartDate(), booking.getStartTime(), booking.getEndTime())) {
                throw new IllegalStateException("Sala are deja cel puțin un loc rezervat în acest interval.");
            }
        } else if (booking.getSeat() != null) {
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

    private Booking createRecurringBookings(CreateBookingRequest request, Integer currentUserId) {
        if (request.startDate() == null || request.endDate() == null || request.endDate().isBefore(request.startDate())) {
            throw new IllegalArgumentException("Data finală a recurenței trebuie să fie după data de început.");
        }
        int interval = request.recurrenceIntervalOfRecurrence() == null ? 1 : request.recurrenceIntervalOfRecurrence();
        if (interval < 1) throw new IllegalArgumentException("Intervalul recurenței trebuie să fie cel puțin 1.");

        String frequency = request.recurrenceFrequency().toLowerCase();
        if (!frequency.equals("zilnic") && !frequency.equals("saptamanal") && !frequency.equals("lunar")) {
            throw new IllegalArgumentException("Frecvența recurenței este invalidă.");
        }

        List<LocalDate> occurrenceDates = new java.util.ArrayList<>();
        LocalDate occurrence = request.startDate();
        while (!occurrence.isAfter(request.endDate())) {
            if (occurrence.getDayOfWeek() != DayOfWeek.SATURDAY && occurrence.getDayOfWeek() != DayOfWeek.SUNDAY) {
                occurrenceDates.add(occurrence);
            }
            occurrence = switch (frequency) {
                case "zilnic" -> occurrence.plusDays(interval);
                case "saptamanal" -> occurrence.plusWeeks(interval);
                default -> occurrence.plusMonths(interval);
            };
        }
        if (occurrenceDates.isEmpty()) {
            throw new IllegalArgumentException("Recurența aleasă conține numai zile de weekend.");
        }

        List<String> conflictingDates = occurrenceDates.stream()
                .map(date -> recurringConflictDescription(request, date))
                .filter(java.util.Objects::nonNull)
                .toList();
        if (!conflictingDates.isEmpty()) {
            throw new IllegalStateException("Seria recurentă nu a fost creată. Locul este ocupat pe: "
                    + String.join(", ", conflictingDates) + ".");
        }

        Booking firstBooking = null;
        for (LocalDate date : occurrenceDates) {
            CreateBookingRequest singleOccurrence = new CreateBookingRequest(
                    request.userId(), request.roomId(), request.seatId(),
                    date, date, request.startTime(), request.endTime(),
                    null, null, null
            );
            Booking created = createBooking(singleOccurrence, currentUserId);
            if (firstBooking == null) firstBooking = created;
        }
        return firstBooking;
    }

    private boolean hasBookingConflict(CreateBookingRequest request, LocalDate date) {
        if (request.seatId() != null) {
            return bookingRepository.existsOverlappingSeatBooking(
                    request.seatId(), date, date, request.startTime(), request.endTime(), null);
        }
        if (request.roomId() != null) {
            return bookingRepository.existsOverlappingBookingInRoom(
                    request.roomId(), date, request.startTime(), request.endTime());
        }
        throw new IllegalArgumentException("Rezervarea trebuie să aibă un loc sau o sală.");
    }

    private String recurringConflictDescription(CreateBookingRequest request, LocalDate date) {
        if (request.seatId() == null) {
            return hasBookingConflict(request, date) ? date.toString() : null;
        }

        Seat seat = seatRepository.findById(request.seatId())
                .orElseThrow(() -> new IllegalArgumentException("Seat not found: " + request.seatId()));
        List<Booking> conflicts = bookingRepository.findConflictsForSeatAtInterval(
                seat.getId(), seat.getRoom().getId(), date, request.startTime(), request.endTime());
        if (conflicts.isEmpty()) return null;

        String occupants = conflicts.stream()
                .map(booking -> booking.getUser().getFirstName() + " " + booking.getUser().getLastName())
                .distinct()
                .reduce((first, second) -> first + ", " + second)
                .orElse("alt utilizator");
        return date + " — " + occupants;
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

        // The edit modal updates only the values that changed (usually date
        // and time). Keep the originally reserved room and seat when those
        // fields are absent from the request.
        if (request.roomId() != null) {
            existing.setRoom(roomRepository.findById(request.roomId())
                    .orElseThrow(() -> new IllegalArgumentException("Room not found: " + request.roomId())));
        }
        if (request.seatId() != null) {
            existing.setSeat(seatRepository.findById(request.seatId())
                    .orElseThrow(() -> new IllegalArgumentException("Seat not found: " + request.seatId())));
        }

        validateBookingInterval(existing);
        if (existing.getSeat() != null) {
            checkSeatAvailability(existing, existing.getId());
        }

        if (request.status() != null) {
            if (request.status() == BookingStatus.FINALIZATA) {
                LocalDateTime reservationEnd = LocalDateTime.of(existing.getEndDate(), existing.getEndTime());
                if (!LocalDateTime.now().isAfter(reservationEnd)) {
                    throw new IllegalStateException("Rezervarea poate fi finalizată numai după ora de încheiere.");
                }
            }
            existing.setStatus(request.status());
        }

        Map<String, Object> updateVars = new HashMap<>();
        updateVars.put("userName", existing.getUser().getFirstName());
        updateVars.put("startDate", existing.getStartDate().toString());
        updateVars.put("startTime", existing.getStartTime().toString());
        updateVars.put("location", existing.getSeat() != null ? "Locul #" + existing.getSeat().getId() : existing.getRoom().getName());
        updateVars.put("status", existing.getStatus());
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
        for (LocalDate date = booking.getStartDate(); !date.isAfter(booking.getEndDate()); date = date.plusDays(1)) {
            if (false && (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY)) {
                throw new IllegalArgumentException("Rezervările nu sunt permise sâmbăta sau duminica");
            }
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
