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
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Transactional
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

        if (!"WEEKLY".equalsIgnoreCase(recurrence.getFrequency())) {
            throw new IllegalArgumentException(
                    "Pentru analytics este suportată momentan doar recurența WEEKLY."
            );
        }

        Set<DayOfWeek> selectedDays = Arrays.stream(
                        recurrence.getDaysOfWeek().split(",")
                )
                .map(String::trim)
                .filter(day -> !day.isBlank())
                .map(String::toUpperCase)
                .map(DayOfWeek::valueOf)
                .collect(Collectors.toSet());

        int interval = recurrence.getIntervalOfRecurrence() == null
                ? 1
                : recurrence.getIntervalOfRecurrence();

        if (interval < 1) {
            throw new IllegalArgumentException(
                    "Intervalul recurenței trebuie să fie cel puțin 1."
            );
        }

        LocalDate firstDate = booking.getStartDate().isAfter(monthStart)
                ? booking.getStartDate()
                : monthStart;

        LocalDate lastDate = booking.getEndDate().isBefore(monthEnd)
                ? booking.getEndDate()
                : monthEnd;

        long total = 0;

        for (LocalDate date = firstDate;
             !date.isAfter(lastDate);
             date = date.plusDays(1)) {

            long weeksFromSeriesStart = ChronoUnit.DAYS
                    .between(booking.getStartDate(), date) / 7;

            boolean isRecurringWeek = weeksFromSeriesStart % interval == 0;
            boolean isSelectedDay = selectedDays.contains(date.getDayOfWeek());

            if (isRecurringWeek && isSelectedDay) {
                total++;
            }
        }

        return total;
    }
}
