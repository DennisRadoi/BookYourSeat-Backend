package entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "building")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor

public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adress_id")
    private Address address;
}
