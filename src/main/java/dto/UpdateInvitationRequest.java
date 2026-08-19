package dto;

public record UpdateInvitationRequest(
        Boolean hasBeenAnswered,
        Boolean answer
) {
}
