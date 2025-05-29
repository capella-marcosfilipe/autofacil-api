package br.com.autofacil.api.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "vendor_sale")
public class VendorSale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User vendor;

    @ManyToOne(optional = false)
    private User buyer;

    @ManyToOne(optional = false)
    private Vehicle vehicle;

    private Double price;

    private LocalDateTime saleDate;

}