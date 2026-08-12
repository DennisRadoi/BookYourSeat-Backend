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
    String preferedFloor;

    @Column(name = "preferred_start_time")
    LocalTime preferredStartTime;

    @Column(name = "preferred_end_time")
    LocalTime preferredEndTime;

    @Column(name = "recieves_notifications_on_email", nullable = false)
    Boolean recieviesNotifOnEmail;

    @Column(name = "preferred_building")
    String prefreedBuilding;

    @Column(name = "near_window", nullable = false)
    Boolean nearWindow;

    @Column(name = "quiet_place", nullable = false)
    Boolean quietPlace;

    @Column(name = "days_of_week")
    String daysOfWeek;
}
