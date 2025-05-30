package br.com.autofacil.api.dtos.purchaserequest;

import io.swagger.v3.oas.annotations.media.Schema;

public record PurchaseRequestRequestDTO(
    @Schema(description = "ID do veículo para o qual a solicitação de compra está sendo feita", example = "1")
    Long vehicleId,
    @Schema(description = "Email do comprador para autenticação", example = "comprador@example.com")
    String buyerEmail,
    @Schema(description = "Senha do comprador para autenticação", example = "senhaDoComprador")
    String buyerPassword
) {
}
