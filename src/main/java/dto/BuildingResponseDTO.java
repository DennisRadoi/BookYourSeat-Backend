package dto;

import entities.Building;

public record BuildingResponseDTO(Integer id, String name, Integer addressId) {

    public static BuildingResponseDTO fromEntity(Building building) {

        return new BuildingResponseDTO(
                building.getId(),
                building.getName(),
                building.getAddress() == null
                        ? null
                        : building.getAddress().getId()
        );
    }
}