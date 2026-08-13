package repositories;

import entities.Locality;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocalityRepository extends JpaRepository<Locality, Integer> {

    List<Locality> findByCountyId(Integer countyId);

    List<Locality> findByCountyName(String countyName);
}