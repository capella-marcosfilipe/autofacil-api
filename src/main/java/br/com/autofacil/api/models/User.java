package br.com.autofacil.api.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true,  nullable = false)
    private String email;

    @Column(name = "password")
    private String passwordHash;
    private String phonenumber;
    private String cpf;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @ManyToMany
    @JoinTable(
            name = "favorites",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "vehicle_id")
    )
    private List<Vehicle> favorites;

    @OneToMany(mappedBy = "vendor")
    private List<VendorSale> vehiclesAsVendor;

    @OneToMany(mappedBy = "buyer")
    private List<VendorSale> vehiclesAsBuyer;
}
