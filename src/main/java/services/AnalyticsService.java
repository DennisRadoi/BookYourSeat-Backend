package services;

import dto.TodayAnalyticsResponse;
import dto.TopBookingResponse;
import dto.WeeklyBookingsResponse;
import dto.WeeklyDayBookingsResponse;
import entities.Booking;
import entities.RecurringBooking;
import entities.User;
import entities.enums.BookingStatus;
import entities.enums.RoomType;
import entities.enums.SeatStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repositories.BookingRepository;
import repositories.RoomRepository;
import repositories.SeatRepository;
import repositories.UserRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final SeatRepository seatRepository;

    @Transactional(readOnly = true)
    public long getTotalBookingsForMonth(int year, int month) {
        YearMonth selectedMonth = YearMonth.of(year, month);

        LocalDate monthStart = selectedMonth.atDay(1);
        LocalDate monthEnd = selectedMonth.atEndOfMonth();
        LocalDate nextMonthStart = selectedMonth.plusMonths(1).atDay(1);

        List<Booking> bookings = bookingRepository
                .findByStartDateLessThanAndEndDateGreaterThanEqualAndStatusNot(
                        nextMonthStart,
                        monthStart,
                        BookingStatus.ANULATA
                );

        return bookings.stream()
                .mapToLong(booking ->
                        countOccurrencesInMonth(booking, monthStart, monthEnd)
                )
                .sum();
    }

    private long countOccurrencesInMonth(
            Booking booking,
            LocalDate monthStart,
            LocalDate monthEnd
    ) {
        RecurringBooking recurrence = booking.getRecurringBooking();

        if (recurrence == null) {
            return 1;
        }

        LocalDate firstDate = booking.getStartDate().isAfter(monthStart)
                ? booking.getStartDate()
                : monthStart;

        LocalDate lastDate = booking.getEndDate().isBefore(monthEnd)
                ? booking.getEndDate()
                : monthEnd;

        String frequency = recurrence.getFrequency()
                .trim()
                .toLowerCase(Locale.ROOT);

        int interval = recurrence.getIntervalOfRecurrence() == null
                ? 1
                : recurrence.getIntervalOfRecurrence();

        if (interval < 1) {
            throw new IllegalArgumentException(
                    "Intervalul recurenței trebuie să fie cel puțin 1."
            );
        }

        return switch (frequency) {
            case "zilnic", "daily" ->
                    countDailyOccurrences(booking, firstDate, lastDate, interval);

            case "saptamanal", "săptămânal", "weekly" ->
                    countWeeklyOccurrences(
                            booking,
                            recurrence,
                            firstDate,
                            lastDate,
                            interval
                    );

            case "lunar", "monthly" ->
                    countMonthlyOccurrences(
                            booking,
                            firstDate,
                            lastDate,
                            interval
                    );

            default -> throw new IllegalArgumentException(
                    "Frecvență necunoscută: " + recurrence.getFrequency()
            );
        };
    }
    private long countDailyOccurrences(
            Booking booking,
            LocalDate firstDate,
            LocalDate lastDate,
            int interval
    ) {
        long total = 0;

        for (LocalDate date = firstDate;
             !date.isAfter(lastDate);
             date = date.plusDays(1)) {

            long daysFromStart = ChronoUnit.DAYS
                    .between(booking.getStartDate(), date);

            if (daysFromStart % interval == 0) {
                total++;
            }
        }

        return total;
    }
    private long countWeeklyOccurrences(
            Booking booking,
            RecurringBooking recurrence,
            LocalDate firstDate,
            LocalDate lastDate,
            int interval
    ) {
        Set<DayOfWeek> selectedDays = getSelectedDays(recurrence, booking);

        long total = 0;

        for (LocalDate date = firstDate;
             !date.isAfter(lastDate);
             date = date.plusDays(1)) {

            long weeksFromStart = ChronoUnit.DAYS
                    .between(booking.getStartDate(), date) / 7;

            boolean isCorrectWeek = weeksFromStart % interval == 0;
            boolean isSelectedDay = selectedDays.contains(date.getDayOfWeek());

            if (isCorrectWeek && isSelectedDay) {
                total++;
            }
        }

        return total;
    }
    private long countMonthlyOccurrences(
            Booking booking,
            LocalDate firstDate,
            LocalDate lastDate,
            int interval
    ) {
        long total = 0;

        YearMonth currentMonth = YearMonth.from(booking.getStartDate());
        YearMonth endMonth = YearMonth.from(lastDate);
        int desiredDayOfMonth = booking.getStartDate().getDayOfMonth();

        while (!currentMonth.isAfter(endMonth)) {
            // Pentru 31 ale lunii: februarie devine 28/29.
            int day = Math.min(desiredDayOfMonth, currentMonth.lengthOfMonth());
            LocalDate occurrence = currentMonth.atDay(day);

            if (!occurrence.isBefore(firstDate) && !occurrence.isAfter(lastDate)) {
                total++;
            }

            currentMonth = currentMonth.plusMonths(interval);
        }

        return total;
    }
    private Set<DayOfWeek> getSelectedDays(
            RecurringBooking recurrence,
            Booking booking
    ) {
        String days = recurrence.getDaysOfWeek();

        if (days == null || days.isBlank()) {
            return Set.of(booking.getStartDate().getDayOfWeek());
        }

        return Arrays.stream(days.split(","))
                .map(String::trim)
                .filter(day -> !day.isBlank())
                .map(this::toDayOfWeek)
                .collect(Collectors.toSet());
    }
    private DayOfWeek toDayOfWeek(String day) {
        return switch (day.trim().toUpperCase(Locale.ROOT)) {
            case "1", "MONDAY", "LUNI" -> DayOfWeek.MONDAY;
            case "2", "TUESDAY", "MARTI", "MARȚI" -> DayOfWeek.TUESDAY;
            case "3", "WEDNESDAY", "MIERCURI" -> DayOfWeek.WEDNESDAY;
            case "4", "THURSDAY", "JOI" -> DayOfWeek.THURSDAY;
            case "5", "FRIDAY", "VINERI" -> DayOfWeek.FRIDAY;
            case "6", "SATURDAY", "SAMBATA", "SÂMBĂTĂ" -> DayOfWeek.SATURDAY;
            case "7", "SUNDAY", "DUMINICA", "DUMINICĂ" -> DayOfWeek.SUNDAY;

            default -> throw new IllegalArgumentException(
                    "Zi de recurență necunoscută: " + day
            );
        };
    }
    @Transactional(readOnly = true)
    public TodayAnalyticsResponse getTodayAnalytics() {
        LocalDate today = LocalDate.now();
        List<Booking> activeBookings = bookingRepository
                .findByStartDateLessThanEqualAndEndDateGreaterThanEqualAndStatusNot(
                        today,
                        today,
                        BookingStatus.ANULATA
                );

        long occupiedConferenceRooms = activeBookings.stream()
                .filter(booking -> booking.getRoom() != null)
                .filter(booking -> booking.getRoom().getType() == RoomType.DE_CONFERINTA)
                .map(booking -> booking.getRoom().getId())
                .distinct()
                .count();

        long totalConferenceRooms = roomRepository
                .countByType(RoomType.DE_CONFERINTA);

        long occupiedOfficeSeats = activeBookings.stream()
                .filter(booking -> booking.getSeat() != null)
                .filter(booking -> booking.getSeat().getRoom().getType() == RoomType.DE_OFICIU)
                .map(booking -> booking.getSeat().getId())
                .distinct()
                .count();

        long totalOfficeSeats = seatRepository
                .countByRoomTypeAndStatus(
                        RoomType.DE_OFICIU,
                        SeatStatus.REZERVABIL
                );

        long peopleInOffice = activeBookings.stream()
                .map(booking -> booking.getUser().getId())
                .distinct()
                .count();

        long conferencePercent = totalConferenceRooms == 0 ? 0
                : Math.round(occupiedConferenceRooms * 100.0 / totalConferenceRooms);

        long officePercent = totalOfficeSeats == 0 ? 0
                : Math.round(occupiedOfficeSeats * 100.0 / totalOfficeSeats);

        return new TodayAnalyticsResponse(
                conferencePercent,
                officePercent,
                peopleInOffice
        );
    }
    @Transactional(readOnly = true)
    public List<TopBookingResponse> getTopBookings(Integer year, Integer month) {
        LocalDate referenceDate = LocalDate.now();
        int targetYear = year != null ? year : referenceDate.getYear();
        int targetMonth = month != null ? month : referenceDate.getMonthValue();

        YearMonth selectedMonth = YearMonth.of(targetYear, targetMonth);
        LocalDate monthStart = selectedMonth.atDay(1);
        LocalDate monthEnd = selectedMonth.atEndOfMonth();
        LocalDate nextMonthStart = selectedMonth.plusMonths(1).atDay(1);

        List<Booking> bookingsInMonth = bookingRepository
                .findByStartDateLessThanAndEndDateGreaterThanEqualAndStatusNot(
                        nextMonthStart,
                        monthStart,
                        BookingStatus.ANULATA
                );

        long totalOfficeSeats = seatRepository
                .countByRoomTypeAndStatus(RoomType.DE_OFICIU, SeatStatus.REZERVABIL);

        Map<Integer, Set<Integer>> seatsByUser = bookingsInMonth.stream()
                .filter(booking -> booking.getSeat() != null)
                .filter(booking -> booking.getSeat().getRoom() != null)
                .filter(booking -> booking.getSeat().getRoom().getType() == RoomType.DE_OFICIU)
                .filter(booking -> booking.getStartDate().isBefore(monthEnd.plusDays(1))
                        && booking.getEndDate().isAfter(monthStart.minusDays(1)))
                .collect(Collectors.groupingBy(
                        booking -> booking.getUser().getId(),
                        Collectors.mapping(booking -> booking.getSeat().getId(), Collectors.toSet())
                ));

        return seatsByUser.entrySet().stream()
                .map(entry -> {
                    User user = userRepository.findById(entry.getKey()).orElse(null);
                    if (user == null) {
                        return null;
                    }

                    long seatCount = entry.getValue().size();
                    double occupancyPercentage = totalOfficeSeats == 0
                            ? 0
                            : (seatCount * 100.0) / totalOfficeSeats;

                    return new TopBookingResponse(
                            user.getFirstName() + " " + user.getLastName(),
                            seatCount,
                            Math.round(occupancyPercentage * 100.0) / 100.0
                    );
                })
                .filter(response -> response != null)
                .sorted(Comparator.comparingLong(TopBookingResponse::seatCount).reversed())
                .toList();
    }

    @Transactional(readOnly = true)
    public WeeklyBookingsResponse getCurrentWeekBookings() {
        LocalDate today = LocalDate.now();

        LocalDate monday = today.with(
                TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
        );
        LocalDate friday = monday.plusDays(4);

        List<Booking> bookings = bookingRepository
                .findByStartDateLessThanEqualAndEndDateGreaterThanEqualAndStatusNot(
                        friday,
                        monday,
                        BookingStatus.ANULATA
                );

        List<WeeklyDayBookingsResponse> days = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            LocalDate date = monday.plusDays(i);

            long officeBookings = bookings.stream()
                    .filter(booking -> occursOnDate(booking, date))
                    .filter(this::isOfficeBooking)
                    .count();

            long conferenceRoomBookings = bookings.stream()
                    .filter(booking -> occursOnDate(booking, date))
                    .filter(this::isConferenceRoomBooking)
                    .count();

            days.add(new WeeklyDayBookingsResponse(
                    getRomanianDayName(date.getDayOfWeek()),
                    officeBookings,
                    conferenceRoomBookings
            ));
        }

        return new WeeklyBookingsResponse(monday, friday, days);
    }

    private boolean occursOnDate(Booking booking, LocalDate date) {
        if (date.isBefore(booking.getStartDate())
                || date.isAfter(booking.getEndDate())) {
            return false;
        }

        RecurringBooking recurrence = booking.getRecurringBooking();

        // O rezervare normala este activa in perioada startDate–endDate.
        if (recurrence == null) {
            return true;
        }

        String frequency = recurrence.getFrequency()
                .trim()
                .toLowerCase(Locale.ROOT);

        int interval = recurrence.getIntervalOfRecurrence() == null
                ? 1
                : recurrence.getIntervalOfRecurrence();

        return switch (frequency) {
            case "zilnic", "daily" ->
                    ChronoUnit.DAYS.between(booking.getStartDate(), date) % interval == 0;

            case "saptamanal", "săptămânal", "weekly" -> {
                long weeksFromStart = ChronoUnit.DAYS
                        .between(booking.getStartDate(), date) / 7;

                boolean isCorrectWeek = weeksFromStart % interval == 0;
                boolean isSelectedDay = getSelectedDays(recurrence, booking)
                        .contains(date.getDayOfWeek());

                yield isCorrectWeek && isSelectedDay;
            }

            case "lunar", "monthly" -> {
                long monthsFromStart = ChronoUnit.MONTHS.between(
                        YearMonth.from(booking.getStartDate()),
                        YearMonth.from(date)
                );

                int expectedDay = Math.min(
                        booking.getStartDate().getDayOfMonth(),
                        YearMonth.from(date).lengthOfMonth()
                );

                yield monthsFromStart >= 0
                        && monthsFromStart % interval == 0
                        && date.getDayOfMonth() == expectedDay;
            }

            default -> false;
        };
    }
    private String getRomanianDayName(DayOfWeek day) {
        return switch (day) {
            case MONDAY -> "Luni";
            case TUESDAY -> "Marți";
            case WEDNESDAY -> "Miercuri";
            case THURSDAY -> "Joi";
            case FRIDAY -> "Vineri";
            case SATURDAY -> "Sâmbătă";
            case SUNDAY -> "Duminică";
        };
    }
    private boolean isOfficeBooking(Booking booking) {
        return booking.getSeat() != null
                || (booking.getRoom() != null
                && booking.getRoom().getType() == RoomType.DE_OFICIU);
    }

    private boolean isConferenceRoomBooking(Booking booking) {
        return booking.getRoom() != null
                && booking.getRoom().getType() == RoomType.DE_CONFERINTA;
    }
}
