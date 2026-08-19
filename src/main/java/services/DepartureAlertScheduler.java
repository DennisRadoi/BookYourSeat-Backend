package services;

import entities.Booking;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import repositories.BookingRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@EnableScheduling // Activează funcționalitatea de automatizare în Spring
@RequiredArgsConstructor
public class DepartureAlertScheduler {

    private final BookingRepository bookingRepository;
    private final EmailService emailService;

    // înseamnă că metoda va rula automat la fiecare minut fix (ex: 10:00:00, 10:01:00)
    @Scheduled(cron = "0 * * * * *")
    public void sendDepartureAlerts() {
        // Calculăm ora exactă de peste 30 de minute
        LocalTime targetTime = LocalTime.now().plusMinutes(30).withSecond(0).withNano(0);
        LocalDate today = LocalDate.now();

        // Căutăm rezervările care încep fix la acea oră
        List<Booking> upcomingBookings = bookingRepository.findUpcomingBookingsStartingAt(today, targetTime);

        for (Booking booking : upcomingBookings) {
            Map<String, Object> vars = new HashMap<>();
            vars.put("userName", booking.getUser().getFirstName());
            vars.put("startTime", booking.getStartTime().toString());
            vars.put("location", booking.getSeat() != null ? "Locul " + booking.getSeat().getId() : booking.getRoom().getName());

            // Trimitem email-ul
            emailService.sendEmail(
                    booking.getUser().getEmail(),
                    "Pregătește-te de plecare! Rezervarea ta începe în 30 minute",
                    "departure-alert",
                    vars
            );
        }
    }
}