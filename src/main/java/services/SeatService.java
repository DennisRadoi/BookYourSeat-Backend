package services;

import entities.Seat;
import entities.enums.SeatStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repositories.BookingRepository;
import repositories.SeatRepository;

import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<Seat> searchAvailableSeats(String status, Boolean nearWindow, Boolean hasMonitor,
                                           Boolean hasStandupDesk, LocalDate date, LocalTime startTime,
                                           LocalTime endTime) {
        SeatStatus seatStatus = parseSeatStatus(status);
        List<Seat> seats = seatRepository.findWithFilters(seatStatus, nearWindow, hasMonitor, hasStandupDesk);
        if (date == null || startTime == null || endTime == null) {
            return seats;
        }

        List<Integer> bookedSeatIds = bookingRepository.findBookedSeatIdsByInterval(date, startTime, endTime);
        return seats.stream()
                .filter(seat -> seat.getId() != null && !bookedSeatIds.contains(seat.getId()))
                .collect(Collectors.toList());
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
