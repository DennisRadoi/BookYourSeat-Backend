package dto;

import entities.Notification;
import entities.UserNotification;

import java.time.OffsetDateTime;

public record GetNotificationResponse(
        Integer id,
        Integer userId,
        String message,
        String type,
        OffsetDateTime createdAt,
        Integer bookingId,
        Integer officeInvitationId,
        Boolean hasBeenRead
) {
    public static GetNotificationResponse fromEntity(UserNotification userNotification) {
        if (userNotification == null) {
            return null;
        }

        Notification notification = userNotification.getNotification();
        Integer uid = null;
        if (userNotification.getUser() != null) {
            uid = userNotification.getUser().getId();
        }
        return new GetNotificationResponse(
                userNotification.getId(),
                uid,
                notification != null ? notification.getMessage() : null,
                notification != null ? notification.getType() : null,
                notification != null ? notification.getCreatedAt() : null,
                notification != null && notification.getBooking() != null ? notification.getBooking().getId() : null,
                notification != null && notification.getOfficeInvitation() != null ? notification.getOfficeInvitation().getId() : null,
                userNotification.getHasBeenRead()
        );
    }
}
