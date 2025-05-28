package br.com.autofacil.api.dtos.vehicle;

import java.math.BigDecimal;
import java.util.List;

public class VehicleRequestDTO {
    private String brand;
    private String model;
    private int year;
    private String color;
    private BigDecimal price;
    private String vehicleType;
    private List<String> photoUrls;
}
