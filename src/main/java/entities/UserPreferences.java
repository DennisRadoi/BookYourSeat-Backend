package entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Table(name = "user_preferences")
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

    @Column(name = "preferred_start_time")
    LocalTime preferredStartTime;

    @Column(name = "preferred_end_time")
    LocalTime preferredEndTime;

    @Column(name = "booking_confirmation_on_email", nullable = false)
    Boolean bookingConfirmationOnEmail;

    @Column(name = "reminder_before_booking", nullable = false)
    Boolean reminderBeforeBooking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preferred_building_id")
    private Building preferredBuilding;

    @Column(name = "near_window", nullable = false)
    Boolean nearWindow;

    @Column(name = "quiet_place", nullable = false)
    Boolean quietPlace;

    @Column(name = "days_of_week")
    String daysOfWeek;
}
