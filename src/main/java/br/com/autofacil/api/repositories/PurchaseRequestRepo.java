package br.com.autofacil.api.repositories;

import br.com.autofacil.api.models.PurchaseRequest;
import br.com.autofacil.api.models.PurchaseRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseRequestRepo extends JpaRepository<PurchaseRequest, Long> {
    List<PurchaseRequest> findByVendorId(Long vendorId);
    List<PurchaseRequest> findByBuyerId(Long buyerId);
    Optional<PurchaseRequest> findByVehicleIdAndStatus(Long vehicleId, PurchaseRequestStatus status);
    List<PurchaseRequest> findByVehicleId(Long vendorId);
}
