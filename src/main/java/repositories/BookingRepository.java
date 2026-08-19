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

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByUserId(Integer userId);

    List<Booking> findByUserIdAndStatus(Integer userId, BookingStatus status);

    // rezervarile viitoare/curente ale userului (nu s-au terminat inca)
    List<Booking> findByUserIdAndEndDateGreaterThanEqual(Integer userId, LocalDate date);

    // rezervarile trecute ale userului (istoric)
    List<Booking> findByUserIdAndEndDateLessThan(Integer userId, LocalDate date);

    // "seria" din spatele unei recurente e reprezentata de un singur Booking (relatie 1-1
    // cu RecurringBooking, aceeasi cheie primara), asa ca aceasta metoda intoarce cel mult 1 rezultat
    List<Booking> findByRecurringBookingId(Integer recurringBookingId);

    @Query("SELECT b FROM Booking b WHERE b.seat.id = :seatId " +
            "AND b.status <> entities.enums.BookingStatus.ANULATA " +
            "AND :date BETWEEN b.startDate AND b.endDate")
    List<Booking> findActiveBookingsBySeatAndDate(@Param("seatId") Integer seatId, @Param("date") LocalDate date);

    @Query("SELECT b.seat.id FROM Booking b WHERE b.seat IS NOT NULL " +
            "AND b.status <> entities.enums.BookingStatus.ANULATA " +
            "AND :date BETWEEN b.startDate AND b.endDate " +
            "AND (:startTime < b.endTime AND :endTime > b.startTime)")
    List<Integer> findBookedSeatIdsByInterval(
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

    // verifica daca acelasi loc are deja o rezervare activa care se suprapune ca perioada de zile si interval orar
    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.seat.id = :seatId " +
            "AND (:excludeBookingId IS NULL OR b.id <> :excludeBookingId) " +
            "AND b.status <> entities.enums.BookingStatus.ANULATA " +
            "AND b.startDate <= :endDate AND b.endDate >= :startDate " +
            "AND b.startTime < :endTime AND b.endTime > :startTime")
    boolean existsOverlappingSeatBooking(
            @Param("seatId") Integer seatId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("excludeBookingId") Integer excludeBookingId
    );

    //cautam in baza de date rezervarile pentru a trimite alerta de 30min
    @Query("SELECT b FROM Booking b WHERE b.status <> entities.enums.BookingStatus.ANULATA " +
            "AND b.startDate = :date " +
            "AND b.startTime = :time")
    List<Booking> findUpcomingBookingsStartingAt(
            @Param("date") LocalDate date,
            @Param("time") LocalTime time);
    List<Booking> findByStartDateLessThanAndEndDateGreaterThanEqualAndStatusNot(
            LocalDate nextMonthStart,
            LocalDate monthStart,
            BookingStatus status
    );
}
