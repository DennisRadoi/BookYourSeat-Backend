package repository;

import entities.User_Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserNotificationRepository extends JpaRepository<User_Notification, Long> {
    List<User_Notification> findByUserIdAndHasBeenReadFalse(Integer userId);
}