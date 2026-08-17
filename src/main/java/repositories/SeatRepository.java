package repositories;

import entities.Seat;
import entities.enums.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Integer> {

    List<Seat> findByRoomId(Integer roomId);

    @Query("SELECT s FROM Seat s WHERE " +
            "(:status IS NULL OR s.status = :status) AND " +
            "(:nearWindow IS NULL OR s.nearWindow = :nearWindow) AND " +
            "(:hasMonitor IS NULL OR s.hasMonitor = :hasMonitor) AND " +
            "(:hasStandupDesk IS NULL OR s.hasStandupDesk = :hasStandupDesk)")
    List<Seat> findWithFilters(
            @Param("status") SeatStatus status,
            @Param("nearWindow") Boolean nearWindow,
            @Param("hasMonitor") Boolean hasMonitor,
            @Param("hasStandupDesk") Boolean hasStandupDesk
    );
}
