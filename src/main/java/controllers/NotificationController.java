package controllers;


import entities.User;
import lombok.RequiredArgsConstructor;
import services.NotificationService;
import dto.GetNotificationResponse;
//import entities.UserNotification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.UserService;

import java.util.List;

@RestController
@RequestMapping("/users/me/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<GetNotificationResponse>> getNotifications(
            @RequestParam(name = "isRead", required = false) Boolean isRead) {
        User u = userService.getCurrentUser();
        return ResponseEntity.ok(notificationService.getUserNotifications(u.getId(), isRead));
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