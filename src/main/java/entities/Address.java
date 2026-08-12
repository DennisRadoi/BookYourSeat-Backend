package entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "adress")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String number;

    private Integer floor;

    @Column(name = "apartment_block")
    private String apartmentBlock;

    @Column(name = "postal_code", nullable = false)
    private String postalCode;

    @Column(nullable = false)
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name =  "locality_id")
    private Locality locality;
}
