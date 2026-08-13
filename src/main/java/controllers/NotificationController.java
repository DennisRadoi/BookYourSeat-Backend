package controllers;


import services.NotificationService;
import dto.NotificationDTO;
//import entities.UserNotification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users/me/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getNotifications(
            @RequestParam(name = "userId", defaultValue = "1") Integer userId,
            @RequestParam(name = "isRead", required = false) Boolean isRead) {
        return ResponseEntity.ok(notificationService.getUserNotifications(userId, isRead).stream()
                .map(NotificationDTO::fromEntity)
                .collect(Collectors.toList()));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @RequestParam(name = "userId", defaultValue = "1") Integer userId,
            @PathVariable Integer id) {
        notificationService.markNotificationAsRead(userId, id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(
            @RequestParam(name = "userId", defaultValue = "1") Integer userId) {
        notificationService.markAllNotificationsAsRead(userId);
        return ResponseEntity.ok().build();
    }
}