package dto;

import entities.OfficeInvitation;
import entities.enums.InvitationStatus;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record InvitationResponse(
        Integer id,
        Integer senderId,
        String senderName,
        Integer receiverId,
        String receiverName,
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
                invitation.getUser().getFirstName() + " " + invitation.getUser().getLastName(),
                invitation.getAddressee().getId(),
                invitation.getAddressee().getFirstName() + " " + invitation.getAddressee().getLastName(),
                invitation.getMessage(),
                invitation.getProposedDate(),
                invitation.getCreatedAt(),
                invitation.getAnsweredAt(),
                invitation.getStatus()
        );
    }
}
