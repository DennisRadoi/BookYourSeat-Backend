package repositories;

import entities.Building;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BuildingRepository extends JpaRepository<Building, Integer> {

    List<Building> findByAddressId(Integer addressId);

    Optional<Building> findByName(String name);

    boolean existsByName(String name);
}