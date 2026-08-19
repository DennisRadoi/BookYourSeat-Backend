package services;

import entities.Seat;
import entities.Booking;
import entities.enums.SeatStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repositories.BookingRepository;
import repositories.SeatRepository;

import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class SeatService {

    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;

    public Seat getSeatById(Integer id) {
        return seatRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Seat not found with id: " + id));
    }

    public List<Seat> getSeatsByRoom(Integer roomId) {
        return seatRepository.findByRoomId(roomId);
    }

    public record SeatAvailability(Seat seat, String occupiedBy) {}

    /**
     * A permanently non-reservable seat keeps its own status. An otherwise
     * reservable seat becomes occupied only for an overlapping booking.
     */
    public List<SeatAvailability> searchSeatsWithAvailability(String status, Boolean nearWindow, Boolean hasMonitor,
                                                               Boolean hasStandupDesk, LocalDate date, LocalTime startTime,
                                                               LocalTime endTime) {
        SeatStatus seatStatus = parseSeatStatus(status);
        List<Seat> seats = seatRepository.findWithFilters(seatStatus, nearWindow, hasMonitor, hasStandupDesk);
        if (date == null || startTime == null || endTime == null) {
            return seats.stream().map(seat -> new SeatAvailability(seat, null)).toList();
        }

        Map<Integer, String> occupantsBySeat = bookingRepository
                .findBookedSeatsWithUsersByInterval(date, startTime, endTime)
                .stream()
                .filter(booking -> booking.getSeat() != null && booking.getSeat().getId() != null)
                .collect(java.util.stream.Collectors.toMap(
                        booking -> booking.getSeat().getId(),
                        booking -> booking.getUser().getFirstName() + " " + booking.getUser().getLastName(),
                        (first, ignored) -> first));
        return seats.stream()
                .map(seat -> new SeatAvailability(seat, seat.getId() == null ? null : occupantsBySeat.get(seat.getId())))
                .toList();
    }

    private SeatStatus parseSeatStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            return SeatStatus.valueOf(status);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Tip de loc necunoscut: " + status);
        }
    }
}
