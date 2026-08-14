package controllers;


import services.NotificationService;
import dto.GetNotificationResponse;
//import entities.UserNotification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/me/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<GetNotificationResponse>> getNotifications(
            @RequestParam(name = "userId") Integer userId,
            @RequestParam(name = "isRead", required = false) Boolean isRead) {
        return ResponseEntity.ok(notificationService.getUserNotifications(userId, isRead));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @RequestParam(name = "userId") Integer userId,
            @PathVariable Integer id) {
        notificationService.markNotificationAsRead(userId, id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(
            @RequestParam(name = "userId") Integer userId) {
        notificationService.markAllNotificationsAsRead(userId);
        return ResponseEntity.ok().build();
    }
}