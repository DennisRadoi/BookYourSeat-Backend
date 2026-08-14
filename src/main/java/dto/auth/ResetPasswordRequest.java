package dto.auth;

public record ResetPasswordRequest(
        String token,
        String newPassword
) {
}
