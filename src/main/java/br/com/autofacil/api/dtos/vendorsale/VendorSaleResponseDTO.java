package br.com.autofacil.api.dtos.vendorsale;

import br.com.autofacil.api.models.VendorSale;

import java.time.LocalDateTime;

public record VendorSaleResponseDTO(
        Long id,
        Long vehicleId,
        Long buyerId,
        Long vendorId,
        Double price,
        LocalDateTime saleDate
) {
    public static VendorSaleResponseDTO fromEntity(VendorSale sale) {
        return new VendorSaleResponseDTO(
                sale.getId(),
                sale.getVehicle().getId(),
                sale.getBuyer().getId(),
                sale.getVendor().getId(),
                sale.getPrice(),
                sale.getSaleDate()
        );
    }
}
