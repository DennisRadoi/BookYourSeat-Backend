package controllers;

import dto.RoomResponseDTO;
import entities.Room;
import entities.enums.RoomType;
import services.RoomService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping("/{id}")
    public RoomResponseDTO getRoomById(@PathVariable Integer id) {

        return roomService.getRoomById(id);
    }

    @GetMapping
    public List<RoomResponseDTO> getAllRooms() {

        return roomService.getAllRooms();
    }

    @PostMapping
    public RoomResponseDTO createRoom(@RequestBody Room room) {

        return roomService.createRoom(room);
    }

    @PutMapping("/{id}")
    public RoomResponseDTO updateRoom(@PathVariable Integer id, @RequestBody Room room) {

        return roomService.updateRoom(id, room);
    }

    @DeleteMapping("/{id}")
    public void deleteRoom(@PathVariable Integer id) {

        roomService.deleteRoom(id);
    }

    @GetMapping("/building/{buildingId}")
    public List<RoomResponseDTO> getRoomsByBuilding(@PathVariable Integer buildingId) {

        return roomService.getRoomsByBuilding(buildingId);
    }

    @GetMapping("/search")
    public List<RoomResponseDTO> searchRooms(@RequestParam String name) {

        return roomService.searchRooms(name);
    }

    @GetMapping("/type/{type}")
    public List<RoomResponseDTO> getRoomsByType(@PathVariable RoomType type) {

        return roomService.getRoomsByType(type);
    }
}