package br.com.autofacil.api.services;

import br.com.autofacil.api.models.Vehicle;
import org.springframework.data.jpa.domain.Specification;

public class VehicleSpecifications {

    public static Specification<Vehicle> hasBrand(String brand) {
        return (root, query, builder) -> builder.like(builder.lower(root.get("brand")), "%" + brand.toLowerCase() + "%");
    }

    public static Specification<Vehicle> hasModel(String model) {
        return (root, query, builder) -> builder.like(builder.lower(root.get("model")), "%" + model.toLowerCase() + "%");
    }

    public static Specification<Vehicle> hasYear(Integer year) {
        return (root, query, builder) -> builder.equal(root.get("year"), year);
    }
}
