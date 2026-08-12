package repository;

import entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByUserId(Integer userId);
    List<Booking> findByRoomIdAndDate(Integer roomId, LocalDate date);
    List<Booking> findBySeatIdAndDate(Integer seatId, LocalDate date);
}
