package dto;

import entities.Notification;
import entities.UserNotification;

import java.time.OffsetDateTime;

public record NotificationDTO(
        Integer id,
        Integer userId,
        String message,
        String type,
        OffsetDateTime createdAt,
        Integer bookingId,
        Boolean hasBeenRead
) {
    public static NotificationDTO fromEntity(UserNotification userNotification) {
        if (userNotification == null) {
            return null;
        }

        Notification notification = userNotification.getNotification();
        Integer uid = null;
        if (userNotification.getUser() != null) {
            uid = userNotification.getUser().getId();
        }
        return new NotificationDTO(
                userNotification.getId(),
                uid,
                notification != null ? notification.getMessage() : null,
                notification != null ? notification.getType() : null,
                notification != null ? notification.getCreatedAt() : null,
                notification != null && notification.getBooking() != null ? notification.getBooking().getId() : null,
                userNotification.getHasBeenRead()
        );
    }
}
