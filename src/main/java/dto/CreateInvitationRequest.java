package dto;

import java.time.LocalDate;

public record CreateInvitationRequest(String message, LocalDate proposedDate) {

}
