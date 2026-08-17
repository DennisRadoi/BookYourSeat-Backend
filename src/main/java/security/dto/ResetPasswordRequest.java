package security.dto;

public record ResetPasswordRequest(
        String token,
        String newPassword
) {
}
