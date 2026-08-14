package security.dto;

public record LoginRequest(
    String email,
    String password
)  {
}
