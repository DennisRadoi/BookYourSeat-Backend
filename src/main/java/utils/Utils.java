package utils;

import entities.Booking;
import entities.RecurringBooking;
import entities.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

public class Utils {
    public static String getRandomFavoriteColleague(List<User> list) {
        int index = new Random().nextInt(list.size());

        User colleague = list.get(index);

        return colleague.getFirstName() + " " + colleague.getLastName().charAt(0) + ".";
    }

    public static boolean isActiveBookingNow(LocalDate today, LocalTime now, Booking booking) {
        boolean isWithinTimeInterval =
                !now.isBefore(booking.getStartTime())
                        && !now.isAfter(booking.getEndTime());

        if (!isWithinTimeInterval) {
            return false;
        }

        RecurringBooking recurring = booking.getRecurringBooking();

        if (recurring == null) {
            return booking.getStartDate().equals(today);
        }

        if (today.isBefore(booking.getStartDate()) || today.isAfter(booking.getEndDate())) {
            return false;
        }

        if ("zilnic".equalsIgnoreCase(recurring.getFrequency())) {
            return true;
        }

        if ("saptamanal".equalsIgnoreCase(recurring.getFrequency())) {

            boolean currentDay = false;
            String currentDayOfWeek = String.valueOf(today.getDayOfWeek().getValue());
            String[] currentRecurringDaysOfWeek = recurring.getDaysOfWeek()
                    .split(",");

            for (String day : currentRecurringDaysOfWeek) {
                if (day.equals(currentDayOfWeek)) {
                    currentDay = true;
                }
            }
            long weeksPassed = ChronoUnit.WEEKS.between(
                    booking.getStartDate(),
                    today
            );

            return currentDay
                    && weeksPassed % recurring.getIntervalOfRecurrence() == 0;
        }
        return false;
    }
}