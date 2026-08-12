package entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.*;

@Entity
@Table(name = "USERS")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {
    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "first_name", nullable = false)
    private String first_name;

    @Column(name = "last_name", nullable = false)
    private String last_name;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone_number", nullable = false, length = 10)
    private String phone_number;

    @Column(name = "phone_number", nullable = false)
    private String profile_photo;

    @Column(name = "password_hash", nullable = false)
    private String password_hash;

    @Column(name = "is_active", nullable = false)
    private Boolean is_active;

    @Column(name = "employment_date", nullable = false)
    private LocalDate employment_date;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private OffsetDateTime created_at;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "adress_id", nullable = false)
    private Adress adress;
}
