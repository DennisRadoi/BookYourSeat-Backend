package services;

import dto.BuildingResponseDTO;
import entities.Building;
import repositories.BuildingRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuildingService {

    private final BuildingRepository buildingRepository;

    private Building findBuildingEntity(Integer id) {

        return buildingRepository.findById(id).orElseThrow(() -> new RuntimeException("Building not found: " + id));
    }

    public BuildingResponseDTO getBuildingById(Integer id) {

        return BuildingResponseDTO.fromEntity(findBuildingEntity(id));
    }

    public List<BuildingResponseDTO> getAllBuildings() {

        return buildingRepository.findAll()
                .stream()
                .map(BuildingResponseDTO::fromEntity)
                .toList();
    }

    public BuildingResponseDTO createBuilding(Building building) {

        return BuildingResponseDTO.fromEntity(buildingRepository.save(building));
    }

    public BuildingResponseDTO updateBuilding(Integer id, Building building) {

        Building existing = findBuildingEntity(id);

        existing.setName(building.getName());
        existing.setAddress(building.getAddress());

        return BuildingResponseDTO.fromEntity(buildingRepository.save(existing));
    }

    public void deleteBuilding(Integer id) {

        buildingRepository.deleteById(id);
    }
}