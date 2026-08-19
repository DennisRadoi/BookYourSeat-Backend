package services;

import entities.Booking;
import entities.RecurringBooking;
import entities.enums.BookingStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repositories.BookingRepository;
import repositories.UserRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

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
}
