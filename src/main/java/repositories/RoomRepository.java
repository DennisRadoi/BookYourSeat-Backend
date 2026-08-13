package repositories;

import entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Integer> {

    List<Room> findByBuildingId(Integer buildingId);

}