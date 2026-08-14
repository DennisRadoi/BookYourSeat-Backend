package services;

import dto.RoomResponseDTO;
import entities.Room;
import entities.enums.RoomType;
import org.springframework.beans.factory.annotation.Autowired;
import repositories.RoomRepository;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    private Room findRoomEntity(Integer id) {

        return roomRepository.findById(id).orElseThrow(() -> new RuntimeException("Room not found!" + id));
    }

    public RoomResponseDTO getRoomById(Integer id) {

        return RoomResponseDTO.fromEntity(
                findRoomEntity(id)
        );
    }

    public List<RoomResponseDTO> getAllRooms() {

        return roomRepository.findAll()
                .stream()
                .map(RoomResponseDTO::fromEntity)
                .toList();
    }

    public RoomResponseDTO createRoom(Room room) {

        return RoomResponseDTO.fromEntity(roomRepository.save(room));
    }

    public RoomResponseDTO updateRoom(Integer id, Room room) {

        Room existing = findRoomEntity(id);

        existing.setName(room.getName());
        existing.setFloor(room.getFloor());
        existing.setType(room.getType());
        existing.setBuilding(room.getBuilding());

        return RoomResponseDTO.fromEntity(roomRepository.save(existing));
    }

    public void deleteRoom(Integer id) {

        roomRepository.deleteById(id);
    }

    public List<RoomResponseDTO> getRoomsByBuilding(Integer buildingId) {

        return roomRepository.findByBuildingId(buildingId)
                .stream()
                .map(RoomResponseDTO::fromEntity)
                .toList();
    }

    public List<RoomResponseDTO> searchRooms(String name) {

        return roomRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(RoomResponseDTO::fromEntity)
                .toList();
    }

    public List<RoomResponseDTO> getRoomsByType(RoomType type) {

        return roomRepository.findByType(type)
                .stream()
                .map(RoomResponseDTO::fromEntity)
                .toList();
    }

}