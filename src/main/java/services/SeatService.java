package services;

import entities.Seat;
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

    public List<Seat> searchAvailableSeats(String type, Boolean nearWindow, Boolean hasMonitor,
                                           Boolean hasStandupDesk, LocalDate date, LocalTime startTime,
                                           LocalTime endTime) {
        List<Seat> seats = seatRepository.findWithFilters(type, nearWindow, hasMonitor, hasStandupDesk);
        if (date == null || startTime == null || endTime == null) {
            return seats;
        }

        List<Integer> bookedSeatIds = bookingRepository.findBookedSeatIdsByInterval(date, startTime, endTime);
        return seats.stream()
                .filter(seat -> seat.getId() != null && !bookedSeatIds.contains(seat.getId()))
                .collect(Collectors.toList());
    }
}
