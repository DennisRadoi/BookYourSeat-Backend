package repositories;

import entities.Booking;
import entities.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByUserId(Integer userId);

    List<Booking> findByUserIdAndDateGreaterThanEqual(Integer userId, LocalDate date);

    List<Booking> findByUserIdAndDateLessThan(Integer userId, LocalDate date);

    List<Booking> findByRecurringBookingId(Integer recurringBookingId);

    @Query("SELECT b FROM Booking b WHERE b.seat.id = :seatId AND b.date = :date AND b.status <> 'anulata'")
    List<Booking> findActiveBookingsBySeatAndDate(@Param("seatId") Integer seatId, @Param("date") LocalDate date);

    @Query("SELECT b.seat.id FROM Booking b WHERE b.date = :date AND b.status <> 'anulata' " +
            "AND (:startTime < b.endTime AND :endTime > b.startTime)")
    List<Integer> findBookedSeatIdsByInterval(
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );
}
