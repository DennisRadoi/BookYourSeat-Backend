package repositories;

import entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import entities.enums.RoomType;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Integer> {

    List<Room> findByBuildingId(Integer buildingId);

    List<Room> findByFloor(Integer floor);

    List<Room> findByType(RoomType type);

    List<Room> findByNameContainingIgnoreCase(String name);

    List<Room> findByBuildingIdAndFloor(Integer buildingId, Integer floor);

    List<Room> findByBuildingIdAndFloorAndType(Integer buildingId, Integer floor, RoomType type);

}