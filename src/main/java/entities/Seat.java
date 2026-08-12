package entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "SEAT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "room_id", nullable = false)
    private Integer roomId;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "type", length = 50)
    private String type;

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