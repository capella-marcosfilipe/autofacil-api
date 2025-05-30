package br.com.autofacil.api.dtos.purchaserequest;

import br.com.autofacil.api.models.PurchaseRequest;
import br.com.autofacil.api.models.PurchaseRequestStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record PurchaseRequestResponseDTO(
    @Schema(description = "ID da solicitação de compra", example = "1")
    Long id,
    @Schema(description = "ID do veículo relacionado à solicitação", example = "1")
    Long vehicleId,
    @Schema(description = "ID do comprador que fez a solicitação", example = "2")
    Long buyerId,
    @Schema(description = "ID do vendedor que recebeu a solicitação", example = "1")
    Long vendorId,
    @Schema(description = "Data e hora em que a solicitação foi criada", example = "2023-10-27T14:00:00")
    LocalDateTime requestDate,
    @Schema(description = "Status atual da solicitação (PENDING, ACCEPTED, DENIED)", example = "PENDING")
    PurchaseRequestStatus status,
    @Schema(description = "Data e hora em que a solicitação foi respondida (se aplicável)", example = "2023-10-27T15:30:00", nullable = true)
    LocalDateTime responseDate
) {
    /**
     * Converte uma entidade PurchaseRequest para um PurchaseRequestResponseDTO.
     *
     * @param purchaseRequest A entidade PurchaseRequest a ser convertida.
     * @return Um PurchaseRequestResponseDTO correspondente.
     */
    public static PurchaseRequestResponseDTO fromEntity(PurchaseRequest purchaseRequest) {
        return new PurchaseRequestResponseDTO(
                purchaseRequest.getId(),
                purchaseRequest.getVehicle().getId(),
                purchaseRequest.getBuyer().getId(),
                purchaseRequest.getVendor().getId(),
                purchaseRequest.getRequestDate(),
                purchaseRequest.getStatus(),
                purchaseRequest.getResponseDate()
        );
    }
}
