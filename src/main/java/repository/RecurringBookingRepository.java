package repository;

import entities.Recurring_Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecurringBookingRepository extends JpaRepository<Recurring_Booking, Integer> {
}