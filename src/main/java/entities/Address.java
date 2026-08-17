package entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "address")
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

    @Column(name = "floor")
    private Integer floor;

    @Column(name = "apartment_block")
    private String apartmentBlock;

    @Column(name = "postal_code", nullable = false, length = 6)
    private String postalCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private AddressType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name =  "locality_id")
    private Locality locality;
}

enum AddressType {
    DE_DOMICILIU,
    DE_OFICIU;
}
