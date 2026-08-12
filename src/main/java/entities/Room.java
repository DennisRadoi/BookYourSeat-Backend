package entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "room")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor

public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer floor;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id")
    private Building building;

    @OneToMany(mappedBy = "seat")
    private List<Seat> seats;
}
