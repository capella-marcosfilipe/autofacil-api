package br.com.autofacil.api.repositories;

import br.com.autofacil.api.models.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepo extends JpaRepository<Vehicle, Long> {
}
