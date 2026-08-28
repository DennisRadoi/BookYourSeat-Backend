package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import entities.User;

@DisplayName("User Controller Tests")
class UserControllerTest extends BaseIntegrationTest {

    private String tokenUserA;
    private String tokenUserB;
    private Integer userBId;

    private static final String EMAIL_A = "usera@test.com";
    private static final String EMAIL_B = "userb@test.com";

    @BeforeEach
    void setUp() throws Exception {
        tokenUserA = registerAndGetToken("Ion", "Popescu", EMAIL_A, VALID_PASSWORD);
        tokenUserB = registerAndGetToken("Maria", "Ionescu", EMAIL_B, VALID_PASSWORD);

        User userB = userRepository.findByEmail(EMAIL_B)
                .orElseThrow(() -> new AssertionError("UserB not found"));
        userBId = userB.getId();
    }

    @Test
    @DisplayName("TC-ONB-01 | GET /departments cu token valid → 200 OK + lista departamente")
    void getDepartments_withValidToken_returns200() throws Exception {
        mockMvc.perform(get("/departments")
                        .header("Authorization", bearerHeader(tokenUserA)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("TC-ONB-02 | GET /departments fara token → 403 Forbidden")
    void getDepartments_withoutToken_returns403() throws Exception {
        mockMvc.perform(get("/departments"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("TC-ONB-03 | PATCH /users/me cu date valide → 200 OK")
    void updateProfile_onboarding_validData_returns200() throws Exception {
        String updateBody = objectMapper.writeValueAsString(Map.of(
                "fullname", "Ion Popescu",
                "phoneNumber", "+40712345678",
                "role", "Developer",
                "employmentDate", "2024-01-15"
        ));

        mockMvc.perform(patch("/users/me")
                        .header("Authorization", bearerHeader(tokenUserA))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("TC-ONB-04 | PATCH /users/me cu numar de telefon invalid → 400 Bad Request")
    void updateProfile_onboarding_invalidPhoneNumber_returns400() throws Exception {
        String updateBody = objectMapper.writeValueAsString(Map.of(
                "fullname", "Ion Popescu",
                "phoneNumber", "0712345678"
        ));

        mockMvc.perform(patch("/users/me")
                        .header("Authorization", bearerHeader(tokenUserA))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    @DisplayName("TC-ONB-05 | PATCH /users/me fara token → 403 Forbidden")
    void updateProfile_onboarding_withoutToken_returns403() throws Exception {
        String updateBody = objectMapper.writeValueAsString(Map.of(
                "fullname", "Ion Popescu"
        ));

        mockMvc.perform(patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("TC-COMP-01 | GET /users cu token valid → 200 OK + lista colegi")
    void getColleagues_withValidToken_returns200() throws Exception {
        mockMvc.perform(get("/users")
                        .header("Authorization", bearerHeader(tokenUserA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("TC-COMP-02 | GET /users fara token → 403 Forbidden")
    void getColleagues_withoutToken_returns403() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("TC-COMP-03 | GET /users cu parametri de paginare → 200 OK + lista paginata")
    void getColleagues_withPaginationParams_returns200() throws Exception {
        mockMvc.perform(get("/users")
                        .param("page", "0")
                        .param("size", "5")
                        .header("Authorization", bearerHeader(tokenUserA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(5));
    }

    @Test
    @DisplayName("TC-PROF-01 | GET /users/{id} cu token valid → 200 OK + detalii coleg")
    void getColleagueProfile_withValidToken_returns200() throws Exception {
        mockMvc.perform(get("/users/" + userBId)
                        .header("Authorization", bearerHeader(tokenUserA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullname").value(containsString("Maria Ionescu")));
    }

    @Test
    @DisplayName("TC-PROF-02 | GET /users/{id} fara token → 403 Forbidden")
    void getColleagueProfile_withoutToken_returns403() throws Exception {
        mockMvc.perform(get("/users/" + userBId))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("TC-INV-01 | POST /users/{id}/office-invitation cu token valid → 201 Created")
    void createInvitation_withValidToken_returns201() throws Exception {
        String invitationBody = objectMapper.writeValueAsString(Map.of(
                "message", "Hai sa lucram impreuna la birou!",
                "proposedDate", "2026-09-10"
        ));

        mockMvc.perform(post("/users/" + userBId + "/office-invitation")
                        .header("Authorization", bearerHeader(tokenUserA))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invitationBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("TC-INV-02 | POST /users/{id}/office-invitation fara token → 403 Forbidden")
    void createInvitation_withoutToken_returns403() throws Exception {
        String invitationBody = objectMapper.writeValueAsString(Map.of(
                "message", "Test",
                "proposedDate", "2026-09-10"
        ));

        mockMvc.perform(post("/users/" + userBId + "/office-invitation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invitationBody))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("TC-CONT-01 | GET /users/me cu token valid → 200 OK + detalii")
    void getMyAccount_withValidToken_returns200() throws Exception {
        mockMvc.perform(get("/users/me")
                        .header("Authorization", bearerHeader(tokenUserA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(EMAIL_A));
    }

    @Test
    @DisplayName("TC-CONT-02 | GET /users/me fara token → 403 Forbidden")
    void getMyAccount_withoutToken_returns403() throws Exception {
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("TC-CONT-03 | PATCH /users/me date valide → 200 OK + date actualizate")
    void updateProfile_validData_returns200() throws Exception {
        String updateBody = objectMapper.writeValueAsString(Map.of(
                "fullname", "Ion Modificat"
        ));

        mockMvc.perform(patch("/users/me")
                        .header("Authorization", bearerHeader(tokenUserA))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Ion"))
                .andExpect(jsonPath("$.lastName").value("Modificat"));
    }

    @Test
    @DisplayName("TC-CONT-04 | PATCH /users/me date invalide (email prost formatat) → 400 Bad Request")
    void updateProfile_invalidEmail_returns400() throws Exception {
        String updateBody = objectMapper.writeValueAsString(Map.of(
                "email", "nu-este-email-valid"
        ));

        mockMvc.perform(patch("/users/me")
                        .header("Authorization", bearerHeader(tokenUserA))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    @DisplayName("TC-CONT-05 | PATCH /users/me fara token → 403 Forbidden")
    void updateProfile_withoutToken_returns403() throws Exception {
        mockMvc.perform(patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("TC-PREF-01 | PATCH /users/me/preferences date valide → 200 OK")
    void updatePreferences_validData_returns200() throws Exception {
        String preferencesBody = objectMapper.writeValueAsString(Map.of(
                "nearWindow", true,
                "quietPlaces", false
        ));

        mockMvc.perform(patch("/users/me/preferences")
                        .header("Authorization", bearerHeader(tokenUserA))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(preferencesBody))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("TC-PREF-02 | PATCH /users/me/preferences fara token → 403 Forbidden")
    void updatePreferences_withoutToken_returns403() throws Exception {
        mockMvc.perform(patch("/users/me/preferences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("TC-SET-01 | GET /users/me/settings cu token valid → 200 OK + detalii")
    void getSettings_withValidToken_returns200() throws Exception {
        mockMvc.perform(get("/users/me/settings")
                        .header("Authorization", bearerHeader(tokenUserA)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("TC-SET-02 | GET /users/me/settings fara token → 403 Forbidden")
    void getSettings_withoutToken_returns403() throws Exception {
        mockMvc.perform(get("/users/me/settings"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("TC-SET-03 | PATCH /users/me/settings/preferences date valide → 200 OK")
    void updateSettings_validData_returns200() throws Exception {
        String settingsBody = objectMapper.writeValueAsString(Map.of(
                "preferredStartTime", "09:00",
                "preferredEndTime", "17:00",
                "receivesNotificationOnEmail", true,
                "isActive", true,
                "daysOfWeek", "MONDAY,TUESDAY,WEDNESDAY"
        ));

        mockMvc.perform(patch("/users/me/settings/preferences")
                        .header("Authorization", bearerHeader(tokenUserA))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(settingsBody))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("TC-SET-04 | PATCH /users/me/settings/preferences fara token → 403 Forbidden")
    void updateSettings_withoutToken_returns403() throws Exception {
        mockMvc.perform(patch("/users/me/settings/preferences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("TC-PWD-01 | PATCH /users/me/change-password cu parola corecta → 200 OK")
    void changePassword_validData_returns200() throws Exception {
        String changePassBody = objectMapper.writeValueAsString(Map.of(
                "currentPassword", VALID_PASSWORD,
                "newPassword", "NewPass@9999"
        ));

        mockMvc.perform(patch("/users/me/change-password")
                        .header("Authorization", bearerHeader(tokenUserA))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(changePassBody))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Password has been modified")));
    }

    @Test
    @DisplayName("TC-PWD-02 | PATCH /users/me/change-password cu parola curenta gresita → 401 Unauthorized")
    void changePassword_wrongCurrentPassword_returns401() throws Exception {
        String changePassBody = objectMapper.writeValueAsString(Map.of(
                "currentPassword", "WrongCurrent@123",
                "newPassword", "NewPass@9999"
        ));

        mockMvc.perform(patch("/users/me/change-password")
                        .header("Authorization", bearerHeader(tokenUserA))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(changePassBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("TC-PWD-03 | PATCH /users/me/change-password cu parola noua invalida → 400 Bad Request")
    void changePassword_invalidNewPassword_returns400() throws Exception {
        String changePassBody = objectMapper.writeValueAsString(Map.of(
                "currentPassword", VALID_PASSWORD,
                "newPassword", "simplu"
        ));

        mockMvc.perform(patch("/users/me/change-password")
                        .header("Authorization", bearerHeader(tokenUserA))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(changePassBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("TC-PWD-04 | PATCH /users/me/change-password fara token → 403 Forbidden")
    void changePassword_withoutToken_returns403() throws Exception {
        mockMvc.perform(patch("/users/me/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }
}
