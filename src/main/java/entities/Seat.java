package entities;

import entities.enums.SeatStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seat")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private SeatStatus status;

    @Column(name = "x_position")
    private Integer xPosition;

    @Column(name = "y_position")
    private Integer yPosition;

    @Column(name = "has_monitor")
    private Boolean hasMonitor;

    @Column(name = "has_docking_station")
    private Boolean hasDockingStation;

    @Column(name = "near_window")
    private Boolean nearWindow;

    @Column(name = "has_standup_desk")
    private Boolean hasStandupDesk;
}
