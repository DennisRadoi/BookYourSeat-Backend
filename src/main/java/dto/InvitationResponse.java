package dto;

import entities.OfficeInvitation;
import entities.enums.InvitationStatus;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record InvitationResponse(
        Integer id,
        Integer senderId,
        Integer receiverId,
        String message,
        LocalDate proposedDate,
        OffsetDateTime createdAt,
        OffsetDateTime answeredAt,
        InvitationStatus status
) {
    public static InvitationResponse fromEntity(OfficeInvitation invitation) {
        return new InvitationResponse(
                invitation.getId(),
                invitation.getUser().getId(),
                invitation.getAddressee().getId(),
                invitation.getMessage(),
                invitation.getProposedDate(),
                invitation.getCreatedAt(),
                invitation.getAnsweredAt(),
                invitation.getStatus()
        );
    }
}
