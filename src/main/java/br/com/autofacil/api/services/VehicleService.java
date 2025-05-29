package br.com.autofacil.api.services;

import br.com.autofacil.api.dtos.vehicle.VehicleRequestDTO;
import br.com.autofacil.api.dtos.vehicle.VehicleResponseDTO;
import br.com.autofacil.api.models.User;
import br.com.autofacil.api.models.Vehicle;
import br.com.autofacil.api.repositories.VehicleRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static br.com.autofacil.api.services.VehicleSpecifications.*;

@Service
public class VehicleService {
    @Autowired private VehicleRepo vehicleRepo;

    // Create
    public VehicleResponseDTO registerVehicle(VehicleRequestDTO dto, User vendor) {
        Vehicle vehicle = new Vehicle();
        vehicle.setBrand(dto.brand());
        vehicle.setModel(dto.model());
        vehicle.setColor(dto.color());
        vehicle.setYear(dto.year());
        vehicle.setPrice(dto.price());
        vehicle.setVehicleType(dto.vehicleType());
        vehicle.setPhotoUrls(dto.photoUrls());
        vehicle.setVendor(vendor);
        vehicle.setSold(false); // Veículo ainda não vendido

        Vehicle saved = vehicleRepo.save(vehicle);
        return VehicleResponseDTO.fromEntity(saved, vendor);
    }

    // Read
    public List<VehicleResponseDTO> findAll(
            Optional<String> brand,
            Optional<String> model,
            Optional<Integer> year
    ) {
        List<Vehicle> vehicles = vehicleRepo.findAll(Sort.by(Sort.Direction.ASC, "id"));
        return vehicles.stream()
                .filter(vehicle -> brand.map(b -> vehicle.getBrand().equalsIgnoreCase(b)).orElse(true))
                .filter(vehicle -> model.map(m -> vehicle.getModel().equalsIgnoreCase(m)).orElse(true))
                .filter(vehicle -> year.map(y -> vehicle.getYear() == y).orElse(true))
                .map(v -> VehicleResponseDTO.fromEntity(v, v.getVendor()))
                .collect(Collectors.toList());
    }

    public Page<VehicleResponseDTO> listVehicles(
            Optional<String> brand,
            Optional<String> model,
            Optional<Integer> year,
            Pageable pageable
    ) {
        Specification<Vehicle> spec = Specification.where(null);

        if (brand.isPresent()) {
            spec = spec.and(hasBrand(brand.get()));
        }
        if (model.isPresent()) {
            spec = spec.and(hasModel(model.get()));
        }
        if (year.isPresent()) {
            spec = spec.and(hasYear(year.get()));
        }

        Page<Vehicle> page = vehicleRepo.findAll(spec, pageable);
        return page.map(vehicle -> VehicleResponseDTO.fromEntity(vehicle, vehicle.getVendor()));
    }

    public Vehicle findById(Long id) {
        return vehicleRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Veículo não encontrado"));
    }

    // Update parcial - Veículo vendido
    public VehicleResponseDTO markAsSold(Long vehicleId) {
        Vehicle vehicle = vehicleRepo.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Veículo não encontrado"));
        vehicle.setSold(true);
        Vehicle saved = vehicleRepo.save(vehicle);
        return VehicleResponseDTO.fromEntity(saved, saved.getVendor());
    }

    // Update completo
    public VehicleResponseDTO updateVehicle(Long id, VehicleRequestDTO dto) {
        Vehicle vehicle = vehicleRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Veículo não encotnrado"));

        vehicle.setBrand(dto.brand());
        vehicle.setModel(dto.model());
        vehicle.setYear(dto.year());
        vehicle.setColor(dto.color());
        vehicle.setPrice(dto.price());
        vehicle.setVehicleType(dto.vehicleType());
        vehicle.setPhotoUrls(dto.photoUrls());

        Vehicle saved = vehicleRepo.save(vehicle);

        return VehicleResponseDTO.fromEntity(saved, saved.getVendor());
    }

    // Delete
    public void deleteVehicle(Long id) {
        if (!vehicleRepo.existsById(id)) {
            throw new EntityNotFoundException("Veículo não encontrado");
        }
        vehicleRepo.deleteById(id);
    }


}
