package repositories;

import entities.County;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CountyRepository extends JpaRepository<County, Integer> {

    Optional<County> findByName(String name);

    boolean existsByName(String name);
}
