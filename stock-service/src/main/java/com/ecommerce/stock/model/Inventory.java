package com.ecommerce.stock.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(name = "inventory")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false, unique = true, length = 40)
    private String productId;

    @Column(name = "available_units", nullable = false)
    private int availableUnits;

    @Column(name = "warehouse_location", nullable = false, length = 160)
    private String warehouseLocation;

    @Column(name = "estimated_delivery", nullable = false, length = 120)
    private String estimatedDelivery;

    protected Inventory() {
    }

    public String getProductId() {
        return productId;
    }

    public int getAvailableUnits() {
        return availableUnits;
    }

    public String getWarehouseLocation() {
        return warehouseLocation;
    }

    public String getEstimatedDelivery() {
        return estimatedDelivery;
    }
}
