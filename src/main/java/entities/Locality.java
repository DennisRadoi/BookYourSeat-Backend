package entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "locality")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor

public class Locality {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "county_id")
    private County county;
}
