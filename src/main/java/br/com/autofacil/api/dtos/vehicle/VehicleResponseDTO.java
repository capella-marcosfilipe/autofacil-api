package br.com.autofacil.api.dtos.vehicle;

import br.com.autofacil.api.models.User;
import br.com.autofacil.api.models.Vehicle;

import java.math.BigDecimal;
import java.util.List;

public record VehicleResponseDTO (
    Long id,
    String brand,
    String model,
    int year,
    String color,
    BigDecimal price,
    String vehicleType,
    boolean sold,
    List<String> photoUrls,
    Long vendorId,
    String vendorName
) {
    public static VehicleResponseDTO fromEntity(Vehicle vehicle, User user) {
        return new VehicleResponseDTO(
                vehicle.getId(),
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getYear(),
                vehicle.getColor(),
                vehicle.getPrice(),
                vehicle.getVehicleType(),
                vehicle.isSold(),
                vehicle.getPhotoUrls(),
                user.getId(),
                user.getName()
        );
    }
}
