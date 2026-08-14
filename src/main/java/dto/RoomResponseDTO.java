package dto;

import entities.Room;

public record RoomResponseDTO(
        Integer id,
        String name,
        Integer floor,
        String type,
        Integer buildingId,
        String buildingName
) {

    public static RoomResponseDTO fromEntity(Room room) {

        return new RoomResponseDTO(
                room.getId(),
                room.getName(),
                room.getFloor(),
                room.getType().name(),
                room.getBuilding() == null ? null : room.getBuilding().getId(),
                room.getBuilding() == null ? null : room.getBuilding().getName()
        );
    }
}