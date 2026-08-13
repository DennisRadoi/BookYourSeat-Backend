package services;

import entities.UserNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repositories.UserNotificationRepository;

import org.springframework.transaction.annotation.Transactional;
//import java.util.HashMap;
import java.util.List;
//import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final UserNotificationRepository userNotificationRepository;

    public List<UserNotification> getUserNotifications(Integer userId, Boolean isRead) {
        if (isRead == null) {
            return userNotificationRepository.findByUserId(userId);
        }
        return userNotificationRepository.findByUserIdAndHasBeenRead(userId, isRead);
    }

    public void markNotificationAsRead(Integer userId, Integer id) {
        UserNotification un = userNotificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + id));
        if (un.getUser() == null || !un.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Notification does not belong to user");
        }
        un.setHasBeenRead(true);
        userNotificationRepository.save(un);
    }

    public void markAllNotificationsAsRead(Integer userId) {
        List<UserNotification> list = userNotificationRepository.findByUserId(userId);
        for (UserNotification un : list) {
            un.setHasBeenRead(true);
        }
        userNotificationRepository.saveAll(list);
    }
}
