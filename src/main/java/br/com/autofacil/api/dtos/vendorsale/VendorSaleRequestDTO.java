package br.com.autofacil.api.dtos.vendorsale;

public record VendorSaleRequestDTO(
        Long vehicleId,
        Long buyerId,
        Double price,
        String vendorEmail,
        String vendorPassword
) {}