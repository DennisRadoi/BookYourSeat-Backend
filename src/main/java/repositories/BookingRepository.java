package repositories;

import entities.Booking;
import entities.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByUserId(Integer userId);
    List<Booking> findByRoomIdAndDate(Integer roomId, LocalDate date);
    List<Booking> findBySeatIdAndDate(Integer seatId, LocalDate date);
    List<Booking> findByUserIdAndStatus(Integer userId, BookingStatus status);
}
