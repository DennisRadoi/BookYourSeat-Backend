package repositories;

import entities.UserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserPreferencesRepository extends JpaRepository<UserPreferences, Integer> {
    Optional<UserPreferences> findByUserId(Integer userId);
    boolean existsByUserId(Integer userId);
    void deleteByUserId(Integer userId);
}
