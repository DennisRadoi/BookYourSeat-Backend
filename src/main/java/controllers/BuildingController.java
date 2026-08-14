package controllers;

import dto.BuildingResponseDTO;
import entities.Building;
import services.BuildingService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/buildings")
@RequiredArgsConstructor
public class BuildingController {

    private final BuildingService buildingService;

    @GetMapping("/{id}")
    public BuildingResponseDTO getBuildingById(@PathVariable Integer id) {

        return buildingService.getBuildingById(id);
    }

    @GetMapping
    public List<BuildingResponseDTO> getAllBuildings() {

        return buildingService.getAllBuildings();
    }

    @PostMapping
    public BuildingResponseDTO createBuilding(@RequestBody Building building) {

        return buildingService.createBuilding(building);
    }

    @PutMapping("/{id}")
    public BuildingResponseDTO updateBuilding(@PathVariable Integer id, @RequestBody Building building) {

        return buildingService.updateBuilding(id, building);
    }

    @DeleteMapping("/{id}")
    public void deleteBuilding(@PathVariable Integer id) {

        buildingService.deleteBuilding(id);
    }
}