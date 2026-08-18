package entities;

import entities.enums.InvitationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "office_invitation")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OfficeInvitation {
    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "addressee_id", nullable = false)
    private User addressee;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private OffsetDateTime createdAt;

    @Column(name = "answered_at")
    private OffsetDateTime answeredAt;

    @Column(name = "proposed_date")
    private LocalDate proposedDate;

    @PrePersist
    public void prePersist() {
        this.createdAt = OffsetDateTime.now();
        this.status = InvitationStatus.IN_ASTEPTARE;
    }

    @PostUpdate
    public void postUpdate() {
        this.answeredAt = OffsetDateTime.now();
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private InvitationStatus status;
}
