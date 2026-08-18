package dto;

import entities.OfficeInvitation;

import java.time.LocalDate;

public record InvitationResponse(Integer senderId,
                                 Integer recieverId,
                                 String message,
                                 LocalDate proposeDate
                                 ) {
    public static InvitationResponse fromEntity(OfficeInvitation invitation) {
        return new InvitationResponse(
                invitation.getUser().getId(),
                invitation.getAddressee().getId(),
                invitation.getMessage(),
                invitation.getProposedDate()
        );
    }
}
