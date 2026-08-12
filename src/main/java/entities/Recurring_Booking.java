package entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "RECURRING_BOOKING")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Recurring_Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "frequency", length = 50)
    private String frequency;

    @Column(name = "days_of_week", length = 100)
    private String daysOfWeek;

    @Column(name = "interval_of_reccur", length = 50)
    private String intervalOfReccur;
}