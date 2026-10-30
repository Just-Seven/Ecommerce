# Ecommerce

## API compuesta de resumen de producto

El módulo `demo` expone `GET /api/v1/product-summary/{id}` y agrega concurrentemente
la información de catálogo, inventario y tipo de cambio. Las URL base se configuran
mediante `CATALOG_SERVICE_URL`, `STOCK_SERVICE_URL` y `EXCHANGE_SERVICE_URL`.

### Ejecución local

Inicia cada comando desde la carpeta indicada, en una terminal independiente:

```powershell
cd catalog-service
mvn spring-boot:run
```

```powershell
cd stock-service
mvn spring-boot:run
```

```powershell
cd exchange-service
mvn spring-boot:run
```

```powershell
cd demo
mvn spring-boot:run
```

Por defecto, catálogo, stock, exchange y la API compuesta escuchan en los puertos
8081, 8082, 8083 y 8080 respectivamente. Consulta el endpoint con:

```http
GET http://localhost:8080/api/v1/product-summary/P-101
```

La respuesta exitosa tiene este contrato:

```json
{
  "product_id": "P-101",
  "title": "Laptop Gamer XYZ",
  "price_usd": 1200.00,
  "price_mxn": 20400.00,
  "stock_status": "IN_STOCK",
  "available_units": 15,
  "currency_conversion_source": "Frankfurter External API",
  "status": "SUCCESS"
}
```

Si uno de los servicios dependientes falla o no está disponible, la API responde
con un estado HTTP de error y un JSON con `status: "ERROR"` y un mensaje.
