package com.nutrilens.nutrition.infrastructure;

import com.nutrilens.nutrition.domain.Food;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodRepository extends JpaRepository<Food, UUID> {
    Optional<Food> findByBarcode(String barcode);
    List<Food> findTop25ByNameContainingIgnoreCaseOrBrandContainingIgnoreCase(String name, String brand);
}

