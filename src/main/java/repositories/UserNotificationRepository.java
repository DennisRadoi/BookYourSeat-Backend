package repositories;

import entities.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, Integer> {
    List<UserNotification> findByUserId(Integer userId);

    List<UserNotification> findByUserIdAndHasBeenRead(Integer userId, Boolean hasBeenRead);
}