package br.com.autofacil.api.controllers;

import br.com.autofacil.api.dtos.vendorsale.VendorSaleRequestDTO;
import br.com.autofacil.api.dtos.vendorsale.VendorSaleResponseDTO;
import br.com.autofacil.api.models.User;
import br.com.autofacil.api.services.UserService;
import br.com.autofacil.api.services.VendorSaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
public class VendorSaleController {

    private final VendorSaleService vendorSaleService;
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Sistema simples de autenticação de usuário apenas para o projeto da faculdade.
     *
     * Autentica o usuário vendedor com base no email e senha fornecidos.
     *
     * @param email O email do vendedor.
     * @param password A senha do vendedor.
     * @return O objeto User autenticado.
     * @throws SecurityException Se o email ou senha estiverem incorretos.
     */
    private User authenticateVendor(String email, String password) {
        User vendor = userService.findByEmail(email)
                .orElseThrow(() -> new SecurityException("Email ou senha incorretos."));

        if (!passwordEncoder.matches(password, vendor.getPasswordHash())) {
            throw new SecurityException("Email ou senha incorretos.");
        }
        return vendor;
    }

    /**
     * Registra uma nova venda de veículo.
     * O vendedor é autenticado via email e senha na requisição.
     *
     * @param dto Os dados da requisição de venda, incluindo credenciais do vendedor.
     * @return ResponseEntity com o DTO da venda registrada e status 201 Created.
     */
    @PostMapping
    public ResponseEntity<VendorSaleResponseDTO> registerSale(
            @RequestBody VendorSaleRequestDTO dto) {
        // Autentica o vendedor primeiro
        User authenticatedVendor = authenticateVendor(dto.vendorEmail(), dto.vendorPassword());

        VendorSaleResponseDTO response = vendorSaleService.registerSale(dto, authenticatedVendor);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retorna uma lista de todas as vendas.
     *
     * @return ResponseEntity com a lista de DTOs de vendas e status 200 OK.
     */
    @GetMapping
    public ResponseEntity<List<VendorSaleResponseDTO>> getAllSales() {
        List<VendorSaleResponseDTO> sales = vendorSaleService.getAllSales();
        return ResponseEntity.ok(sales);
    }

    /**
     * Retorna uma venda específica pelo ID.
     *
     * @param id O ID da venda.
     * @return ResponseEntity com o DTO da venda e status 200 OK.
     */
    @GetMapping("/{id}")
    public ResponseEntity<VendorSaleResponseDTO> getSaleById(@PathVariable Long id) {
        VendorSaleResponseDTO sale = vendorSaleService.getSaleById(id);
        return ResponseEntity.ok(sale);
    }

    /**
     * Atualiza uma venda existente.
     * O vendedor é autenticado via email e senha na requisição.
     *
     * @param id O ID da venda a ser atualizada.
     * @param dto Os dados da requisição de venda com as informações atualizadas, incluindo credenciais do vendedor.
     * @return ResponseEntity com o DTO da venda atualizada e status 200 OK.
     */
    @PutMapping("/{id}")
    public ResponseEntity<VendorSaleResponseDTO> updateSale(
            @PathVariable Long id,
            @RequestBody VendorSaleRequestDTO dto) {
        // Autentica o vendedor primeiro
        User authenticatedVendor = authenticateVendor(dto.vendorEmail(), dto.vendorPassword());

        VendorSaleResponseDTO updatedSale = vendorSaleService.updateSale(id, dto, authenticatedVendor);
        return ResponseEntity.ok(updatedSale);
    }

    /**
     * Exclui uma venda pelo ID.
     *
     * @param id O ID da venda a ser excluída.
     * @return ResponseEntity com status 204 No Content.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSale(@PathVariable Long id) {
        vendorSaleService.deleteSale(id);
        return ResponseEntity.noContent().build();
    }
}
