package services;

import dto.GetNotificationResponse;
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

    public java.util.List<GetNotificationResponse> getUserNotifications(Integer userId, Boolean isRead) {
        java.util.List<UserNotification> entities;
        if (isRead == null) {
            entities = userNotificationRepository.findByUserIdOrderByNotification_CreatedAtDesc(userId);
        } else {
            entities = userNotificationRepository.findByUserIdAndHasBeenReadOrderByNotification_CreatedAtDesc(userId, isRead);
        }
        return entities.stream().map(GetNotificationResponse::fromEntity).toList();
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
        List<UserNotification> list = userNotificationRepository.findByUserIdOrderByNotification_CreatedAtDesc(userId);
        for (UserNotification un : list) {
            un.setHasBeenRead(true);
        }
        userNotificationRepository.saveAll(list);
    }
}
