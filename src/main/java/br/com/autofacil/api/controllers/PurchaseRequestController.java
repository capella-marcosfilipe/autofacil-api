package br.com.autofacil.api.controllers;

import br.com.autofacil.api.dtos.purchaserequest.PurchaseRequestRequestDTO;
import br.com.autofacil.api.dtos.purchaserequest.PurchaseRequestResponseDTO;
import br.com.autofacil.api.dtos.CredentialsDTO;
import br.com.autofacil.api.models.User;
import br.com.autofacil.api.models.UserRole;
import br.com.autofacil.api.services.AuthenticationService;
import br.com.autofacil.api.services.PurchaseRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/purchase-requests")
@RequiredArgsConstructor
@Tag(name = "Solicitações de Compra", description = "Endpoints para gerenciamento de solicitações de compra de veículos")
public class PurchaseRequestController {

    private final PurchaseRequestService purchaseRequestService;
    private final AuthenticationService authService;

    @Operation(
            summary = "Criar uma nova solicitação de compra",
            description = "Permite que um usuário BUYER crie uma solicitação de compra para um veículo.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Solicitação de compra criada com sucesso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PurchaseRequestResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Requisição inválida (ex: veículo já vendido, solicitação pendente existente, comprador é o próprio vendedor)",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
                    @ApiResponse(responseCode = "401", description = "Não autorizado (credenciais do comprador inválidas)",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
                    @ApiResponse(responseCode = "404", description = "Veículo não encontrado",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
            }
    )
    @PostMapping
    public ResponseEntity<PurchaseRequestResponseDTO> createPurchaseRequest(
            @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados da solicitação de compra e credenciais do comprador",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PurchaseRequestRequestDTO.class))
            ) PurchaseRequestRequestDTO dto) {
        // Autentica o comprador
        User authenticatedBuyer = authService.authenticateAndVerifyRole(
                dto.buyerEmail(),
                dto.buyerPassword(),
                UserRole.BUYER
        );

        PurchaseRequestResponseDTO response = purchaseRequestService.createPurchaseRequest(dto.vehicleId(), authenticatedBuyer);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Obter solicitação de compra por ID",
            description = "Retorna os detalhes de uma solicitação de compra específica pelo seu ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Solicitação encontrada com sucesso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PurchaseRequestResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Solicitação não encontrada",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<PurchaseRequestResponseDTO> getPurchaseRequestById(
            @Parameter(description = "ID da solicitação de compra a ser buscada", example = "1")
            @PathVariable Long id) {
        PurchaseRequestResponseDTO purchaseRequest = purchaseRequestService.getPurchaseRequestById(id);
        return ResponseEntity.ok(purchaseRequest);
    }

    @Operation(
            summary = "Listar solicitações de compra feitas por um comprador (via POST)",
            description = "Retorna todas as solicitações de compra que um usuário BUYER fez. As credenciais são enviadas no corpo da requisição por segurança."
    )
    @PostMapping("/query/by-buyer")
    public ResponseEntity<List<PurchaseRequestResponseDTO>> getPurchaseRequestsByBuyer(
            @RequestBody CredentialsDTO dto) {
        // Autenticar usuário BUYER
        User authenticatedBuyer = authService.authenticateAndVerifyRole(
                dto.email(),
                dto.password(),
                UserRole.BUYER
        );

        List<PurchaseRequestResponseDTO> requests = purchaseRequestService.getPurchaseRequestsByBuyer(authenticatedBuyer.getId());

        return ResponseEntity.ok(requests);
    }

    @Operation(
            summary = "Listar solicitações de compra recebidas por um vendedor (via POST)",
            description = "Retorna todas as solicitações de compra que um usuário VENDOR recebeu. As credenciais são enviadas no corpo da requisição por segurança."
    )
    @PostMapping("/query/by-vendor")
    public ResponseEntity<List<PurchaseRequestResponseDTO>> getPurchaseRequestsByVendor(
            @RequestBody CredentialsDTO dto) {

        // Autentica usuário VENDOR
        User authenticatedVendor = authService.authenticateAndVerifyRole(
                dto.email(),
                dto.password(),
                UserRole.VENDOR
        );

        List<PurchaseRequestResponseDTO> requests = purchaseRequestService.getPurchaseRequestsByVendor(authenticatedVendor.getId());

        return ResponseEntity.ok(requests);
    }

    @Operation(
            summary = "Aceitar uma solicitação de compra",
            description = "Permite que um usuário VENDOR aceite uma solicitação de compra pendente, registrando a venda do veículo.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Solicitação aceita e venda registrada com sucesso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PurchaseRequestResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Requisição inválida (ex: solicitação não pendente, veículo já vendido)",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
                    @ApiResponse(responseCode = "401", description = "Não autorizado (credenciais do vendedor inválidas ou vendedor não é o proprietário da solicitação)",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
                    @ApiResponse(responseCode = "404", description = "Solicitação de compra não encontrada",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
            }
    )
    @PutMapping("/{id}/accept")
    public ResponseEntity<PurchaseRequestResponseDTO> acceptPurchaseRequest(@PathVariable Long id, @RequestBody Map<String, String> credentials) {
        User authenticatedVendor = authService.authenticateAndVerifyRole(
                credentials.get("vendorEmail"),
                credentials.get("vendorPassword"),
                UserRole.VENDOR
        );

        PurchaseRequestResponseDTO response = purchaseRequestService.acceptPurchaseRequest(id, authenticatedVendor);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Negar uma solicitação de compra",
            description = "Permite que um usuário VENDOR negue uma solicitação de compra pendente.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Solicitação negada com sucesso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PurchaseRequestResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Requisição inválida (ex: solicitação não pendente)",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
                    @ApiResponse(responseCode = "401", description = "Não autorizado (credenciais do vendedor inválidas ou vendedor não é o proprietário da solicitação)",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
                    @ApiResponse(responseCode = "404", description = "Solicitação de compra não encontrada",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
            }
    )
    @PutMapping("/{id}/deny")
    public ResponseEntity<PurchaseRequestResponseDTO> denyPurchaseRequest(
            @Parameter(description = "ID da solicitação de compra a ser negada", example = "1")
            @PathVariable Long id,
            @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Credenciais do vendedor para autenticação",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Map.class, example = "{\"vendorEmail\": \"vendedor@example.com\", \"vendorPassword\": \"senhaDoVendedor\"}"))
            ) Map<String, String> credentials) {
        // Autentica o vendedor
        User authenticatedVendor = authService.authenticateAndVerifyRole(
                credentials.get("vendorEmail"),
                credentials.get("vendorPassword"),
                UserRole.VENDOR
        );

        PurchaseRequestResponseDTO response = purchaseRequestService.denyPurchaseRequest(id, authenticatedVendor);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Excluir uma solicitação de compra",
            description = "Exclui uma solicitação de compra pelo seu ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Solicitação excluída com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Solicitação não encontrada",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePurchaseRequest(
            @Parameter(description = "ID da solicitação de compra a ser excluída", example = "1")
            @PathVariable Long id) {
        purchaseRequestService.deletePurchaseRequest(id);
        return ResponseEntity.noContent().build();
    }
}
