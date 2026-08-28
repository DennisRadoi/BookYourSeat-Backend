package integration;

import entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@DisplayName("Auth Controller Tests")
class AuthControllerTest extends BaseIntegrationTest {

    private static final String REGISTER_URL = "/auth/register";
    private static final String LOGIN_URL = "/auth/login";
    private static final String FORGOT_PASSWORD_URL = "/auth/forgot-password";
    private static final String RESET_PASSWORD_URL = "/auth/reset-password";

    private String validRegisterBody;

    @BeforeEach
    void setUp() throws Exception {
        validRegisterBody = objectMapper.writeValueAsString(Map.of(
                "firstName", "Ion",
                "lastName", "Popescu",
                "email", TEST_EMAIL,
                "password", VALID_PASSWORD,
                "phoneNumber", "+40712345678"
        ));
    }

    @Test
    @DisplayName("TC-REG-01 | Register cu date valide → 201 Created + token JWT")
    void register_validData_returns201AndToken() throws Exception {
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRegisterBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.token", not(emptyString())));
    }

    @Test
    @DisplayName("TC-REG-02 | Register cu email deja existent → 409 Conflict")
    void register_existingEmail_returns409() throws Exception {
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRegisterBody))
                .andExpect(status().isCreated());

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRegisterBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("TC-REG-03 | Register cu parola invalida (fara simbol) → 400 Bad Request")
    void register_invalidPassword_noSymbol_returns400() throws Exception {
        String bodyInvalidPassword = objectMapper.writeValueAsString(Map.of(
                "firstName", "Ion",
                "lastName", "Popescu",
                "email", "altEmail@test.com",
                "password", "Password1"
        ));

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyInvalidPassword))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        containsString("Parola trebuie să aibă minimum 8 caractere")));
    }

    @Test
    @DisplayName("TC-REG-04 | Register cu parola prea scurta (<8 caractere) → 400 Bad Request")
    void register_invalidPassword_tooShort_returns400() throws Exception {
        String bodyShortPassword = objectMapper.writeValueAsString(Map.of(
                "firstName", "Ion",
                "lastName", "Popescu",
                "email", "altEmail2@test.com",
                "password", "Ab@1"
        ));

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyShortPassword))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("TC-LOGIN-01 | Login cu email si parola corecte → 200 OK + token JWT")
    void login_validCredentials_returns200AndToken() throws Exception {
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRegisterBody))
                .andExpect(status().isCreated());

        String loginBody = objectMapper.writeValueAsString(Map.of(
                "email", TEST_EMAIL,
                "password", VALID_PASSWORD
        ));

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.token", not(emptyString())));
    }

    @Test
    @DisplayName("TC-LOGIN-02 | Login cu email inexistent → 401 Unauthorized")
    void login_wrongEmail_returns401() throws Exception {
        String loginBody = objectMapper.writeValueAsString(Map.of(
                "email", "email.inexistent@test.com",
                "password", VALID_PASSWORD
        ));

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(containsString("nu există")));
    }

    @Test
    @DisplayName("TC-LOGIN-03 | Login cu parola incorecta → 401 Unauthorized")
    void login_wrongPassword_returns401() throws Exception {
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRegisterBody))
                .andExpect(status().isCreated());

        String loginBody = objectMapper.writeValueAsString(Map.of(
                "email", TEST_EMAIL,
                "password", "WrongPass@999"
        ));

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(containsString("incorectă")));
    }

    @Test
    @DisplayName("TC-FP-01 | Forgot password cu email existent → 200 OK")
    void forgotPassword_existingEmail_returns200() throws Exception {
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRegisterBody))
                .andExpect(status().isCreated());

        String forgotBody = objectMapper.writeValueAsString(Map.of("email", TEST_EMAIL));

        mockMvc.perform(post(FORGOT_PASSWORD_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(forgotBody))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("TC-FP-02 | Forgot password cu email inexistent → 404 Not Found")
    void forgotPassword_nonexistentEmail_returns404() throws Exception {
        String forgotBody = objectMapper.writeValueAsString(Map.of("email", "inexistent@test.com"));

        mockMvc.perform(post(FORGOT_PASSWORD_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(forgotBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("TC-RP-01 | Reset password cu token valid si parola valida → 200 OK")
    void resetPassword_validToken_returns200() throws Exception {
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRegisterBody))
                .andExpect(status().isCreated());

        String forgotBody = objectMapper.writeValueAsString(Map.of("email", TEST_EMAIL));
        mockMvc.perform(post(FORGOT_PASSWORD_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(forgotBody))
                .andExpect(status().isOk());

        User user = userRepository.findByEmail(TEST_EMAIL)
                .orElseThrow(() -> new AssertionError("Userul nu a fost gasit in baza de date"));
        String resetToken = user.getResetToken();

        org.junit.jupiter.api.Assertions.assertNotNull(resetToken, "Token-ul de resetare trebuie sa fie generat");

        String resetBody = objectMapper.writeValueAsString(Map.of(
                "token", resetToken,
                "newPassword", "NewPass@5678"
        ));

        mockMvc.perform(post(RESET_PASSWORD_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resetBody))
                .andExpect(status().isOk());

        User userAfterReset = userRepository.findByEmail(TEST_EMAIL)
                .orElseThrow(() -> new AssertionError("Userul nu a fost gasit dupa reset"));
        org.junit.jupiter.api.Assertions.assertNull(
                userAfterReset.getResetToken(),
                "Token-ul trebuie sters dupa resetare"
        );
    }

    @Test
    @DisplayName("TC-RP-02 | Reset password cu token invalid → 400 Bad Request")
    void resetPassword_invalidToken_returns400() throws Exception {
        String resetBody = objectMapper.writeValueAsString(Map.of(
                "token", "token-invalid-inexistent-12345",
                "newPassword", "NewPass@5678"
        ));

        mockMvc.perform(post(RESET_PASSWORD_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resetBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("invalid")));
    }

    @Test
    @DisplayName("TC-RP-03 | Reset password cu token valid dar parola invalida → 400 Bad Request")
    void resetPassword_validToken_invalidPassword_returns400() throws Exception {
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRegisterBody))
                .andExpect(status().isCreated());

        mockMvc.perform(post(FORGOT_PASSWORD_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", TEST_EMAIL))))
                .andExpect(status().isOk());

        String resetToken = userRepository.findByEmail(TEST_EMAIL)
                .map(User::getResetToken)
                .orElseThrow(() -> new AssertionError("Token nu a fost generat"));

        String resetBody = objectMapper.writeValueAsString(Map.of(
                "token", resetToken,
                "newPassword", "simplu"
        ));

        mockMvc.perform(post(RESET_PASSWORD_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resetBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        containsString("Parola trebuie să aibă minimum 8 caractere")));
    }
}
