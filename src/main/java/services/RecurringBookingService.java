package services;

import entities.RecurringBooking;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repositories.RecurringBookingRepository;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RecurringBookingService {

    private final RecurringBookingRepository recurringBookingRepository;

    public RecurringBooking getById(Integer id) {
        return recurringBookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recurring booking not found: " + id));
    }

    public RecurringBooking create(RecurringBooking recurringBooking) {
        return recurringBookingRepository.save(recurringBooking);
    }

    public RecurringBooking update(Integer id, RecurringBooking recurringBooking) {
        RecurringBooking existing = getById(id);
        existing.setFrequency(recurringBooking.getFrequency());
        existing.setDaysOfWeek(recurringBooking.getDaysOfWeek());
        existing.setIntervalOfRecurrence(recurringBooking.getIntervalOfRecurrence());
        return recurringBookingRepository.save(existing);
    }

    public List<RecurringBooking> findAll() {
        return recurringBookingRepository.findAll();
    }
}
