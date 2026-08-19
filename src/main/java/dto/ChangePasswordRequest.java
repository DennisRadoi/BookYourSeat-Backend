package dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotNull String currentPassword,
        @NotNull @Size(min = 8, message = "Trebuie sa aiba minim 8 caractere.") String newPassword) {
}
