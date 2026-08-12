package entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Table(name = "USER_PREFERENCES")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserPreferences {
    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "preferred_floor")
    String prefered_floor;

    @Column(name = "preferred_start_time")
    LocalTime preferred_start_time;

    @Column(name = "preferred_end_time")
    LocalTime preferred_end_time;

    @Column(name = "recieves_notifications_on_email", nullable = false)
    Boolean recievies_notif_on_email;

    @Column(name = "preferred_building")
    String prefreed_building;

    @Column(name = "near_window", nullable = false)
    Boolean near_window;

    @Column(name = "quiet_place", nullable = false)
    Boolean quiet_place;

    @Column(name = "days_of_week")
    String days_of_week;
}
