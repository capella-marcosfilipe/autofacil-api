package br.com.autofacil.api.repositories;

import br.com.autofacil.api.models.VendorSale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendorSaleRepo extends JpaRepository<VendorSale, Long> {
}
