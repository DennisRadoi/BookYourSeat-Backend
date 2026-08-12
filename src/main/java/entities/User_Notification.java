package entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "USER_NOTIFICATION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class User_Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "has_been_read", nullable = false)
    private Boolean hasBeenRead;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id", nullable = false)
    private Notification notification;
}