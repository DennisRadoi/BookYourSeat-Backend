package entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "county")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class County {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;
}
