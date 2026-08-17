package dto;

import java.time.LocalDate;

public record CreateInvitationRequest(Integer recieverId,
                                      String message, LocalDate proposedDate) {

}
