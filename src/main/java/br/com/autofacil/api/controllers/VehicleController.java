package br.com.autofacil.api.controllers;

import br.com.autofacil.api.dtos.vehicle.VehicleCreationRequestDTO;
import br.com.autofacil.api.dtos.vehicle.VehicleRequestDTO;
import br.com.autofacil.api.dtos.vehicle.VehicleResponseDTO;
import br.com.autofacil.api.models.User;
import br.com.autofacil.api.models.UserRole;
import br.com.autofacil.api.models.Vehicle;
import br.com.autofacil.api.services.AuthenticationService;
import br.com.autofacil.api.services.UserService;
import br.com.autofacil.api.services.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/vehicles")
@RequiredArgsConstructor
public class VehicleController {
    private final VehicleService vehicleService;
    private final AuthenticationService authService;

    // CREATE
    @PostMapping
    public ResponseEntity<VehicleResponseDTO> create(@RequestBody VehicleCreationRequestDTO dto) {
        // Check if user is VENDOR
        User authenticatedVendor = authService.authenticateAndVerifyRole(
                dto.vendorEmail(),
                dto.vendorPassword(),
                UserRole.VENDOR
        );

        // Call method with authenticatedVendor
        VehicleResponseDTO created = vehicleService.registerVehicle(dto, authenticatedVendor);
        return  ResponseEntity.ok(created);
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<Page<VehicleResponseDTO>> list(
            @RequestParam Optional<String> brand,
            @RequestParam Optional<String> model,
            @RequestParam Optional<Integer> year,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<VehicleResponseDTO> vehicles = vehicleService.listVehicles(brand, model, year, pageable);
        return ResponseEntity.ok(vehicles);
    }

    // READ by ID
    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponseDTO> getById(@PathVariable Long id) {
        Vehicle found = vehicleService.findById(id);

        return ResponseEntity.ok(new VehicleResponseDTO(
                found.getId(),
                found.getBrand(),
                found.getModel(),
                found.getYear(),
                found.getColor(),
                found.getPrice(),
                found.getVehicleType(),
                found.isSold(),
                found.getPhotoUrls(),
                found.getVendor().getId(),
                found.getVendor().getName()
        ));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponseDTO> update(@PathVariable Long id,
                                                     @RequestBody VehicleRequestDTO dto) {
        VehicleResponseDTO updated = vehicleService.updateVehicle(id, dto);
        return ResponseEntity.ok(updated);
    }

    // UPDATE status (sold)
    @PatchMapping("/{id}/sold")
    public ResponseEntity<VehicleResponseDTO> markAsSold(@PathVariable Long id) {
        VehicleResponseDTO updated = vehicleService.markAsSold(id);
        return ResponseEntity.ok(updated);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }

}
