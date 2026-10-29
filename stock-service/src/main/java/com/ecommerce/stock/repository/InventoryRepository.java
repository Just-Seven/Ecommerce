package com.ecommerce.stock.repository;

import com.ecommerce.stock.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
}
