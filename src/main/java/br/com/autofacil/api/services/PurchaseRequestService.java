package br.com.autofacil.api.services;

import br.com.autofacil.api.dtos.purchaserequest.PurchaseRequestResponseDTO;
import br.com.autofacil.api.dtos.vendorsale.VendorSaleRequestDTO;
import br.com.autofacil.api.models.PurchaseRequest;
import br.com.autofacil.api.models.PurchaseRequestStatus;
import br.com.autofacil.api.models.User;
import br.com.autofacil.api.models.Vehicle;
import br.com.autofacil.api.repositories.PurchaseRequestRepo;
import br.com.autofacil.api.repositories.UserRepo;
import br.com.autofacil.api.repositories.VehicleRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseRequestService {
    private final PurchaseRequestRepo purchaseRequestRepo;
    private final UserRepo userRepo;
    private final VehicleRepo vehicleRepo;
    private final VendorSaleService vendorSaleService;

    /**
     * Cria uma nova solicitação de compra.
     *
     * @param vehicleId O ID do veículo que está sendo solicitado.
     * @param buyer O usuário que está fazendo a solicitação (comprador).
     * @return O DTO de resposta da solicitação de compra criada.
     * @throws EntityNotFoundException Se o veículo não for encontrado.
     * @throws IllegalStateException Se o veículo já estiver vendido ou já tiver uma solicitação pendente.
     * @throws IllegalArgumentException Se o comprador for o próprio vendedor do veículo.
     */
    @Transactional
    public PurchaseRequestResponseDTO createPurchaseRequest(Long vehicleId, User buyer) {
        // Busca o veículo
        Vehicle vehicle = vehicleRepo.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Veículo com ID " + vehicleId + " não encontrado."));

        // Verifica se o veículo já foi vendido
        if (vehicle.isSold()) {
            throw new IllegalStateException("Veículo com ID " + vehicleId + " já foi vendido e não pode receber solicitações de compra.");
        }

        // Verifica se já existe uma solicitação PENDENTE para este veículo
        if (purchaseRequestRepo.findByVehicleIdAndStatus(vehicleId, PurchaseRequestStatus.PENDING).isPresent()) {
            throw new IllegalStateException("Já existe uma solicitação de compra PENDENTE para o veículo com ID " + vehicleId + ".");
        }

        // Verifica se o comprador não é o próprio vendedor do veículo
        if (vehicle.getVendor().getId().equals(buyer.getId())) {
            throw new IllegalArgumentException("O comprador não pode fazer uma solicitação de compra para seu próprio veículo.");
        }

        // Cria a nova solicitação de compra
        PurchaseRequest purchaseRequest = new PurchaseRequest(vehicle, buyer, vehicle.getVendor());
        purchaseRequest.setRequestDate(LocalDateTime.now());
        purchaseRequest.setStatus(PurchaseRequestStatus.PENDING);

        PurchaseRequest savedRequest = purchaseRequestRepo.save(purchaseRequest);
        return PurchaseRequestResponseDTO.fromEntity(savedRequest);
    }

    /**
     * Retorna uma solicitação de compra pelo seu ID.
     *
     * @param id O ID da solicitação de compra.
     * @return O DTO de resposta da solicitação.
     * @throws EntityNotFoundException Se a solicitação não for encontrada.
     */
    public PurchaseRequestResponseDTO getPurchaseRequestById(Long id) {
        PurchaseRequest purchaseRequest = purchaseRequestRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação de compra com ID " + id + " não encontrada."));
        return PurchaseRequestResponseDTO.fromEntity(purchaseRequest);
    }

    /**
     * Retorna todas as solicitações de compra feitas por um comprador específico.
     * Útil para o dashboard do comprador.
     *
     * @param buyerId O ID do comprador.
     * @return Uma lista de DTOs de resposta de solicitações de compra.
     * @throws EntityNotFoundException Se o comprador não for encontrado.
     */
    public List<PurchaseRequestResponseDTO> getPurchaseRequestsByBuyer(Long buyerId) {
        return purchaseRequestRepo.findByBuyerId(buyerId).stream()
                .map(PurchaseRequestResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Retorna todas as solicitações de compra recebidas por um vendedor específico.
     * Útil para o dashboard do vendedor.
     *
     * @param vendorId O ID do vendedor.
     * @return Uma lista de DTOs de resposta de solicitações de compra.
     * @throws EntityNotFoundException Se o vendedor não for encontrado.
     */
    public List<PurchaseRequestResponseDTO> getPurchaseRequestsByVendor(Long vendorId) {
        return purchaseRequestRepo.findByVendorId(vendorId).stream()
                .map(PurchaseRequestResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Aceita uma solicitação de compra. Isso resultará no registro de uma venda.
     *
     * @param purchaseRequestId O ID da solicitação de compra a ser aceita.
     * @param currentVendor O usuário vendedor que está aceitando a solicitação.
     * @return O DTO de resposta da solicitação de compra atualizada.
     * @throws EntityNotFoundException Se a solicitação não for encontrada.
     * @throws SecurityException Se o vendedor atual não for o vendedor da solicitação.
     * @throws IllegalStateException Se a solicitação não estiver no status PENDING.
     */
    @Transactional
    public PurchaseRequestResponseDTO acceptPurchaseRequest(Long purchaseRequestId, User currentVendor) {
        PurchaseRequest purchaseRequest = purchaseRequestRepo.findById(purchaseRequestId)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação de compra com ID " + purchaseRequestId + " não encontrada."));

        // Verifica se o vendedor logado é o responsável por esta solicitação
        if (!purchaseRequest.getVendor().getId().equals(currentVendor.getId())) {
            throw new SecurityException("Usuário não autorizado para aceitar esta solicitação de compra.");
        }

        // Verifica se a solicitação está pendente
        if (purchaseRequest.getStatus() != PurchaseRequestStatus.PENDING) {
            throw new IllegalStateException("A solicitação de compra com ID " + purchaseRequestId + " não está pendente e não pode ser aceita.");
        }

        // Verifica se o veículo já foi vendido por alguma outra forma (garantia extra)
        if (purchaseRequest.getVehicle().isSold()) {
            throw new IllegalStateException("O veículo desta solicitação (ID " + purchaseRequest.getVehicle().getId() + ") já foi vendido.");
        }

        // Cria um DTO de venda para registrar a venda através do VendorSaleService
        VendorSaleRequestDTO vendorSaleDto = new VendorSaleRequestDTO(
                purchaseRequest.getVehicle().getId(),
                purchaseRequest.getBuyer().getId(),
                purchaseRequest.getVehicle().getPrice().doubleValue(), // Usando o preço do veículo como preço da venda
                currentVendor.getEmail(), // Credenciais do vendedor para o serviço de venda
                null // A senha não é necessária aqui, pois o 'currentVendor' já está autenticado
        );

        // Registra a venda - esta chamada vai marcar o veículo como vendido
        vendorSaleService.registerSale(vendorSaleDto, currentVendor);

        // Atualiza o status da solicitação de compra para ACEITA
        purchaseRequest.setStatus(PurchaseRequestStatus.ACCEPTED);
        purchaseRequest.setResponseDate(LocalDateTime.now());
        PurchaseRequest updatedRequest = purchaseRequestRepo.save(purchaseRequest);

        return PurchaseRequestResponseDTO.fromEntity(updatedRequest);
    }

    /**
     * Nega uma solicitação de compra.
     *
     * @param purchaseRequestId O ID da solicitação de compra a ser negada.
     * @param currentVendor O usuário vendedor que está negando a solicitação.
     * @return O DTO de resposta da solicitação de compra atualizada.
     * @throws EntityNotFoundException Se a solicitação não for encontrada.
     * @throws SecurityException Se o vendedor atual não for o vendedor da solicitação.
     * @throws IllegalStateException Se a solicitação não estiver no status PENDING.
     */
    @Transactional
    public PurchaseRequestResponseDTO denyPurchaseRequest(Long purchaseRequestId, User currentVendor) {
        PurchaseRequest purchaseRequest = purchaseRequestRepo.findById(purchaseRequestId)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação de compra com ID " + purchaseRequestId + " não encontrada."));

        // Verifica se o vendedor logado é o responsável por esta solicitação
        if (!purchaseRequest.getVendor().getId().equals(currentVendor.getId())) {
            throw new SecurityException("Usuário não autorizado para negar esta solicitação de compra.");
        }

        // Verifica se a solicitação está pendente
        if (purchaseRequest.getStatus() != PurchaseRequestStatus.PENDING) {
            throw new IllegalStateException("A solicitação de compra com ID " + purchaseRequestId + " não está pendente e não pode ser negada.");
        }

        // Atualiza o status da solicitação de compra para NEGADA
        purchaseRequest.setStatus(PurchaseRequestStatus.DENIED);
        purchaseRequest.setResponseDate(LocalDateTime.now());
        PurchaseRequest updatedRequest = purchaseRequestRepo.save(purchaseRequest);

        return PurchaseRequestResponseDTO.fromEntity(updatedRequest);
    }

    /**
     * Exclui uma solicitação de compra.
     * @param id O ID da solicitação a ser excluída.
     * @throws EntityNotFoundException Se a solicitação não for encontrada.
     */
    @Transactional
    public void deletePurchaseRequest(Long id) {
        if (!purchaseRequestRepo.existsById(id)) {
            throw new EntityNotFoundException("Solicitação de compra com ID " + id + " não encontrada.");
        }
        purchaseRequestRepo.deleteById(id);
    }
}
