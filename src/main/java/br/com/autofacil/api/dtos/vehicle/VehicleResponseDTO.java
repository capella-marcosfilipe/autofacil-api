package br.com.autofacil.api.dtos.vehicle;

import java.math.BigDecimal;
import java.util.List;

public class VehicleResponseDTO {
    private Long id;
    private String brand;
    private String model;
    private int year;
    private String color;
    private BigDecimal price;
    private String vehicleType;
    private boolean sold;
    private List<String> photoUrls;
    private Long vendorId;
    private String vendorName;
}
