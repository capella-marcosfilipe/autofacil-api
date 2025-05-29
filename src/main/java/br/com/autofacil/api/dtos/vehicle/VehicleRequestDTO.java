package br.com.autofacil.api.dtos.vehicle;

import java.math.BigDecimal;
import java.util.List;

public record VehicleRequestDTO (
    String brand,
    String model,
    int year,
    String color,
    BigDecimal price,
    String vehicleType,
    List<String> photoUrls
) {}
