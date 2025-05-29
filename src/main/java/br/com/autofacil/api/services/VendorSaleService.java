package br.com.autofacil.api.services;

import br.com.autofacil.api.dtos.vendorsale.VendorSaleRequestDTO;
import br.com.autofacil.api.dtos.vendorsale.VendorSaleResponseDTO;
import br.com.autofacil.api.models.User;
import br.com.autofacil.api.models.Vehicle;
import br.com.autofacil.api.models.VendorSale;
import br.com.autofacil.api.repositories.UserRepo;
import br.com.autofacil.api.repositories.VehicleRepo;
import br.com.autofacil.api.repositories.VendorSaleRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VendorSaleService {
    private final VendorSaleRepo vendorSaleRepo;
    private final VehicleRepo vehicleRepo;
    private final UserRepo userRepo;

    /**
     * Registra uma nova venda de veículo.
     *
     * @param dto Os dados da requisição de venda.
     * @param vendor O usuário que está realizando a venda (o vendedor do veículo).
     * @return O DTO de resposta da venda registrada.
     * @throws EntityNotFoundException Se o veículo ou o comprador não forem encontrados.
     * @throws SecurityException Se o vendedor não for o proprietário do veículo.
     * @throws IllegalStateException Se o veículo já tiver sido vendido.
     */
    @Transactional
    public VendorSaleResponseDTO registerSale(VendorSaleRequestDTO dto, User vendor) {
        // Validação manual do preço
        if (dto.price() == null || dto.price() <= 0) {
            throw new IllegalArgumentException("O preço deve ser um valor positivo.");
        }

        // Fetch vehicle by id.
        Vehicle vehicle = vehicleRepo.findById(dto.vehicleId()).orElseThrow(() -> new EntityNotFoundException("Veículo com o ID " + dto.vehicleId() + " não encontrado."));

        // Checking requirements: the vendor is the owner of the vehicle; the vehicle hasn't been sold.
        if (!vehicle.getVendor().getId().equals(vendor.getId())) {
            throw new SecurityException("Usuário não autorizado para vender este veículo. Você não é o proprietário.");
        }

        if (vehicle.isSold()) {
            throw new IllegalStateException("Veículo com ID " + dto.vehicleId() + " já foi vendido.");
        }

        // Fetch buyer by id
        User buyer = userRepo.findById(dto.buyerId())
                .orElseThrow(() -> new EntityNotFoundException("Comprador com ID " + dto.buyerId() + " não encontrado."));

        // Update vehicle status to 'sold'.
        vehicle.setSold(true);
        vehicleRepo.save(vehicle);

        // Create a new sale
        VendorSale sale = new VendorSale();
        sale.setVehicle(vehicle);
        sale.setVendor(vendor);
        sale.setBuyer(buyer);
        sale.setPrice(dto.price());
        sale.setSaleDate(LocalDateTime.now());

        // Save and return
        VendorSale saved = vendorSaleRepo.save(sale);

        return VendorSaleResponseDTO.fromEntity(saved);
    }

    /**
     * Retorna uma lista de todas as vendas registradas.
     *
     * @return Uma lista de DTOs de resposta de vendas.
     */
    public List<VendorSaleResponseDTO> getAllSales() {
        return vendorSaleRepo.findAll().stream()
                .map(VendorSaleResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Retorna uma venda específica pelo seu ID.
     *
     * @param id O ID da venda.
     * @return O DTO de resposta da venda encontrada.
     * @throws EntityNotFoundException Se a venda não for encontrada.
     */
    public VendorSaleResponseDTO getSaleById(Long id) {
        VendorSale sale = vendorSaleRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Venda com ID " + id + " não encontrada."));
        return VendorSaleResponseDTO.fromEntity(sale);
    }

    /**
     * Atualiza uma venda existente.
     * Nota: A atualização é limitada ao comprador e ao preço.
     * O veículo e o vendedor da venda são considerados imutáveis após a criação.
     * Data da venda não pode ser alterada.
     *
     * @param id O ID da venda a ser atualizada.
     * @param dto Os dados da requisição de venda com as informações atualizadas.
     * @param vendor O usuário que está realizando a atualização (para fins de autorização, se necessário).
     * @return O DTO de resposta da venda atualizada.
     * @throws EntityNotFoundException Se a venda ou o novo comprador não forem encontrados.
     * @throws SecurityException Se o vendedor não for o proprietário da venda (se a regra de negócio exigir).
     */
    @Transactional
    public VendorSaleResponseDTO updateSale(Long id, VendorSaleRequestDTO dto, User vendor) {
        // Fetch sale by id
        VendorSale existingSale = vendorSaleRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Venda com ID " + id + " não encontrada."));

        // Fetch new buyer by id
        User newBuyer = userRepo.findById(dto.buyerId())
                .orElseThrow(() -> new EntityNotFoundException("Novo comprador com ID " + dto.buyerId() + " não encontrado."));

        // Update allowed fields: buyer and price
        existingSale.setBuyer(newBuyer);
        existingSale.setPrice(dto.price());

        // Save and return
        VendorSale updatedSale = vendorSaleRepo.save(existingSale);
        return VendorSaleResponseDTO.fromEntity(updatedSale);
    }

    /**
     * Exclui uma venda pelo seu ID.
     *
     * @param id O ID da venda a ser excluída.
     * @throws EntityNotFoundException Se a venda não for encontrada.
     * @throws IllegalStateException Se a venda não puder ser excluída (ex: veículo precisa ter status 'sold' revertido).
     */
    @Transactional
    public void deleteSale(Long id) {
        VendorSale saleToDelete = vendorSaleRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Venda com ID " + id + " não encontrada."));

        // Fetch vehicle. Vehicle must be set back to 'not sold'.
        Vehicle vehicle = saleToDelete.getVehicle();
        if (vehicle != null) {
            vehicle.setSold(false);
            vehicleRepo.save(vehicle);
        }

        vendorSaleRepo.delete(saleToDelete);
    }
}
