package entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "recurring_booking")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class RecurringBooking {

    @Id
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private Booking booking;

    @Column(name = "frequency", length = 50)
    private String frequency;

    @Column(name = "days_of_week", length = 100)
    private String daysOfWeek;

    @Column(name = "interval_of_recurrence", length = 50)
    private Integer intervalOfRecurrence;
}