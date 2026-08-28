package integration;

import entities.Notification;
import entities.User;
import entities.UserNotification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@DisplayName("Notification Controller Tests")
class NotificationControllerTest extends BaseIntegrationTest {

    private String tokenUserA;
    private String tokenUserB;
    private Integer userAId;

    private static final String EMAIL_A = "notif.usera@test.com";
    private static final String EMAIL_B = "notif.userb@test.com";
    private static final String NOTIFICATIONS_URL = "/users/me/notifications";

    @BeforeEach
    void setUp() throws Exception {
        tokenUserA = registerAndGetToken("Notif", "UserA", EMAIL_A, VALID_PASSWORD);
        tokenUserB = registerAndGetToken("Notif", "UserB", EMAIL_B, VALID_PASSWORD);

        User userA = userRepository.findByEmail(EMAIL_A)
                .orElseThrow(() -> new AssertionError("UserA not found"));
        userAId = userA.getId();
    }

    private Integer createUnreadNotificationForUserA(String message) {
        User userA = userRepository.findByEmail(EMAIL_A)
                .orElseThrow(() -> new RuntimeException("UserA not found"));

        Notification notification = new Notification();
        notification.setUser(userA);
        notification.setType("test_notificare");
        notification.setMessage(message);
        notification = notificationRepository.save(notification);

        UserNotification userNotification = new UserNotification();
        userNotification.setUser(userA);
        userNotification.setNotification(notification);
        userNotification.setHasBeenRead(false);
        userNotification = userNotificationRepository.save(userNotification);

        return userNotification.getId();
    }

    @Test
    @DisplayName("TC-NOTIF-01 | GET /users/me/notifications fara filtru → 200 OK")
    void getNotifications_all_returns200() throws Exception {
        createUnreadNotificationForUserA("Notificare 1");
        createUnreadNotificationForUserA("Notificare 2");

        mockMvc.perform(get(NOTIFICATIONS_URL)
                        .header("Authorization", bearerHeader(tokenUserA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(greaterThanOrEqualTo(2)));
    }

    @Test
    @DisplayName("TC-NOTIF-02 | GET /users/me/notifications?isRead=false → 200 OK")
    void getNotifications_unread_returns200() throws Exception {
        createUnreadNotificationForUserA("Notificare necitita");

        mockMvc.perform(get(NOTIFICATIONS_URL)
                        .param("isRead", "false")
                        .header("Authorization", bearerHeader(tokenUserA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].hasBeenRead").value(false));
    }

    @Test
    @DisplayName("TC-NOTIF-03 | GET /users/me/notifications gol → 200 OK + lista goala")
    void getNotifications_newUser_returnsEmptyList() throws Exception {
        mockMvc.perform(get(NOTIFICATIONS_URL)
                        .header("Authorization", bearerHeader(tokenUserB)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("TC-NOTIF-04 | PUT /users/me/notifications/{id}/read → 200 OK")
    void markAsRead_ownNotification_returns200AndIsRead() throws Exception {
        Integer notifId = createUnreadNotificationForUserA("Notificare de marcat ca citita");

        mockMvc.perform(put(NOTIFICATIONS_URL + "/" + notifId + "/read")
                        .header("Authorization", bearerHeader(tokenUserA)))
                .andExpect(status().isOk());

        mockMvc.perform(get(NOTIFICATIONS_URL)
                        .param("isRead", "false")
                        .header("Authorization", bearerHeader(tokenUserA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + notifId + ")]").doesNotExist());

        mockMvc.perform(get(NOTIFICATIONS_URL)
                        .param("isRead", "true")
                        .header("Authorization", bearerHeader(tokenUserA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + notifId + ")].hasBeenRead").value(contains(true)));
    }

    @Test
    @DisplayName("TC-NOTIF-05 | PUT /users/me/notifications/{id}/read notificare alt user → 400/403/404 eroare")
    void markAsRead_otherUsersNotification_returnsError() throws Exception {
        Integer notifIdOfUserA = createUnreadNotificationForUserA("Notificarea lui UserA");

        mockMvc.perform(put(NOTIFICATIONS_URL + "/" + notifIdOfUserA + "/read")
                        .header("Authorization", bearerHeader(tokenUserB)))
                .andExpect(status().is(anyOf(equalTo(400), equalTo(403), equalTo(404))));

        mockMvc.perform(get(NOTIFICATIONS_URL)
                        .param("isRead", "false")
                        .header("Authorization", bearerHeader(tokenUserA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + notifIdOfUserA + ")]").exists());
    }

    @Test
    @DisplayName("TC-NOTIF-06 | PUT /users/me/notifications/{id}/read deja citita → 200 OK")
    void markAsRead_alreadyRead_returns200() throws Exception {
        Integer notifId = createUnreadNotificationForUserA("Notificare deja citita");

        mockMvc.perform(put(NOTIFICATIONS_URL + "/" + notifId + "/read")
                        .header("Authorization", bearerHeader(tokenUserA)))
                .andExpect(status().isOk());

        mockMvc.perform(put(NOTIFICATIONS_URL + "/" + notifId + "/read")
                        .header("Authorization", bearerHeader(tokenUserA)))
                .andExpect(status().isOk());

        mockMvc.perform(get(NOTIFICATIONS_URL)
                        .param("isRead", "true")
                        .header("Authorization", bearerHeader(tokenUserA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + notifId + ")].hasBeenRead").value(contains(true)));
    }

    @Test
    @DisplayName("TC-NOTIF-07 | PUT /users/me/notifications/read-all → 200 OK")
    void markAllAsRead_returns200AndAllRead() throws Exception {
        createUnreadNotificationForUserA("Notificare 1");
        createUnreadNotificationForUserA("Notificare 2");
        createUnreadNotificationForUserA("Notificare 3");

        mockMvc.perform(put(NOTIFICATIONS_URL + "/read-all")
                        .header("Authorization", bearerHeader(tokenUserA)))
                .andExpect(status().isOk());

        mockMvc.perform(get(NOTIFICATIONS_URL)
                        .param("isRead", "false")
                        .header("Authorization", bearerHeader(tokenUserA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        mockMvc.perform(get(NOTIFICATIONS_URL)
                        .param("isRead", "true")
                        .header("Authorization", bearerHeader(tokenUserA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(greaterThanOrEqualTo(3)));
    }

    @Test
    @DisplayName("TC-NOTIF-08 | PUT /users/me/notifications/read-all gol → 200 OK")
    void markAllAsRead_userWithNoNotifications_returns200() throws Exception {
        mockMvc.perform(put(NOTIFICATIONS_URL + "/read-all")
                        .header("Authorization", bearerHeader(tokenUserB)))
                .andExpect(status().isOk());

        mockMvc.perform(get(NOTIFICATIONS_URL)
                        .header("Authorization", bearerHeader(tokenUserB)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("TC-NOTIF-09 | GET /users/me/notifications fara token → 403 Forbidden")
    void getNotifications_withoutToken_returns403() throws Exception {
        mockMvc.perform(get(NOTIFICATIONS_URL))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("TC-NOTIF-10 | PUT /users/me/notifications/{id}/read fara token → 403 Forbidden")
    void markAsRead_withoutToken_returns403() throws Exception {
        mockMvc.perform(put(NOTIFICATIONS_URL + "/1/read"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("TC-NOTIF-11 | PUT /users/me/notifications/read-all fara token → 403 Forbidden")
    void markAllAsRead_withoutToken_returns403() throws Exception {
        mockMvc.perform(put(NOTIFICATIONS_URL + "/read-all"))
                .andExpect(status().isForbidden());
    }
}
