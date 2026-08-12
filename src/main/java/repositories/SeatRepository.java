package repositories;

import entities.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Integer> {
    List<Seat> findByRoomId(Integer roomId);
    List<Seat> findByRoomIdAndStatus(Integer roomId, String status);
}