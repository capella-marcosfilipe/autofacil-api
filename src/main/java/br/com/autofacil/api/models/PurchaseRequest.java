package br.com.autofacil.api.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Esta entidade representa uma solicitação de compra de um veículo.
 * Gerencia o fluxo de solicitação, aceitação e negação da compra.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "purchase_request")
public class PurchaseRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // O veículo que está sendo solicitado para compra
    @ManyToOne(optional = false)
    @JoinColumn(name = "vehicle_id") // Coluna de chave estrangeira
    private Vehicle vehicle;

    // O usuário que fez a solicitação de compra
    @ManyToOne(optional = false)
    @JoinColumn(name = "buyer_id") // Coluna de chave estrangeira
    private User buyer;

    // O vendedor do veículo (que receberá/responderá à solicitação)
    @ManyToOne(optional = false)
    @JoinColumn(name = "vendor_id") // Coluna de chave estrangeira
    private User vendor;

    // Data e hora em que a solicitação foi criada
    @Column(name = "request_date", nullable = false)
    private LocalDateTime requestDate;

    // Status atual da solicitação (PENDING, ACCEPTED, DENIED)
    @Enumerated(EnumType.STRING) // Armazena o enum como String no banco de dados
    @Column(name = "status", nullable = false)
    private PurchaseRequestStatus status;

    // Data e hora em que a solicitação foi respondida (aceita ou negada)
    @Column(name = "response_date")
    private LocalDateTime responseDate;

    // Construtor adicional para conveniência ao criar uma nova solicitação
    public PurchaseRequest(Vehicle vehicle, User buyer, User vendor) {
        this.vehicle = vehicle;
        this.buyer = buyer;
        this.vendor = vendor;
        this.requestDate = LocalDateTime.now(); // Define a data de criação automaticamente
        this.status = PurchaseRequestStatus.PENDING; // Define o status inicial como PENDENTE
    }
}
