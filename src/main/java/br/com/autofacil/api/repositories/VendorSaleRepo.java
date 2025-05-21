package br.com.autofacil.api.repositories;

import br.com.autofacil.api.models.VendorSale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VendorSaleRepo extends JpaRepository<VendorSale, Long> {
    List<VendorSale> findByVendorId(Long vendorId);
}
