package repository;

import entities.RecurringBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecurringBookingRepository extends JpaRepository<RecurringBooking, Integer> {
}