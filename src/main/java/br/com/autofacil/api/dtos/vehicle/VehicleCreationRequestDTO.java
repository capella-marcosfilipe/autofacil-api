package br.com.autofacil.api.dtos.vehicle;

import java.math.BigDecimal;
import java.util.List;

/**
 * Este DTO é específico e distinto do VehicleRequestDTO em razão da regra de negócio na qual é necessária
 * a credencial do usuário tipo VENDOR para poder criar um veículo.
 *
 * @param brand
 * @param model
 * @param year
 * @param color
 * @param price
 * @param vehicleType
 * @param photoUrls
 * @param vendorEmail
 * @param vendorPassword
 */
public record VehicleCreationRequestDTO (
        // Campos do veículo
        String brand,
        String model,
        int year,
        String color,
        BigDecimal price,
        String vehicleType,
        List<String> photoUrls,

        // Credenciais do Vendedor
        String vendorEmail,
        String vendorPassword
) {}