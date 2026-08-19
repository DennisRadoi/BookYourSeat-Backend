package repositories;

import entities.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, Integer> {
    // query by user id
    List<UserNotification> findByUserIdOrderByNotification_CreatedAtDesc(Integer userId);

    List<UserNotification> findByUserIdAndHasBeenReadOrderByNotification_CreatedAtDesc(Integer userId, Boolean hasBeenRead);
}
