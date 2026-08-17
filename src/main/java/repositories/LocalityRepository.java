package repositories;

import entities.Locality;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LocalityRepository extends JpaRepository<Locality, Integer> {

    List<Locality> findByCountyId(Integer countyId);

    List<Locality> findByCountyName(String countyName);

    boolean existsByName(String name);

    Optional<Locality> findByName(String name);

    boolean existsByNameAndCountyId(String name, Integer countyId);
    Optional<Locality> findByNameAndCountyName(String name, String countyName);
}