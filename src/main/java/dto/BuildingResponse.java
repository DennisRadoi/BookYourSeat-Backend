package dto;

import entities.Building;

public record BuildingResponse(String name) {
    public BuildingResponse fromEntity(Building building) {
        return new BuildingResponse(name);
    }
}
