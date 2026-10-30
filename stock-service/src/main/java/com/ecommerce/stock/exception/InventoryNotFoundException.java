package com.ecommerce.stock.exception;

public class InventoryNotFoundException extends RuntimeException {

    public InventoryNotFoundException(String productId) {
        super("No se encontró una consulta de inventario para el producto " + productId);
    }
}
