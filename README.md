# E-Commerce Gateway & Dashboard — Composite API

## 1. Descripción del proyecto

La aplicación implementa una API compuesta para una pantalla de detalle de producto. En lugar de que el frontend consulte por separado el catálogo, el inventario y el tipo de cambio, el cliente realiza una sola petición al agregador. El agregador consulta sus dependencias concurrentemente, transforma los datos y responde con un contrato JSON unificado.

El patrón **Composite API / API Aggregator** reduce la cantidad de llamadas desde el cliente y centraliza la orquestación y el manejo de errores de las dependencias.

## 2. Arquitectura y APIs

| Servicio | Tipo | Endpoint | Puerto local | Función |
| --- | --- | --- | ---: | --- |
| Catálogo | Propio | `GET /microservices/products/{id}` | 8081 | Devuelve nombre, descripción, categoría y precio base en USD. |
| Stock | Propio | `GET /microservices/stock/{productId}` | 8082 | Devuelve estado, unidades disponibles, almacén y entrega estimada. |
| Exchange | Adaptador propio de API externa | `GET /microservices/exchange?from=USD&to=MXN` | 8083 | Consulta Frankfurter y devuelve la tasa, fecha y fuente. |
| Composite API (`demo`) | Propio, agregador | `GET /api/v1/product-summary/{id}` | 8080 | Combina las tres respuestas y calcula el precio en MXN. |

El endpoint del servicio de stock se denomina `/microservices/stock/{productId}` en esta implementación.

### Servicio externo de divisas

El microservicio `exchange-service` consume Frankfurter mediante:

```text
GET https://api.frankfurter.dev/v2/rate/{base}/{quote}
```

Por ejemplo, para USD a MXN, solicita la tasa `usd/mxn`. El agregador consulta el endpoint local del servicio exchange; no consume directamente la API externa.

## 3. Contrato de la API compuesta

### Petición

```http
GET http://localhost:8080/api/v1/product-summary/P-101
Accept: application/json
```

### Respuesta exitosa

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

`price_mxn` se calcula multiplicando `price_usd` por la tasa USD/MXN y redondeando a dos decimales. El tipo de cambio cambia con la respuesta del proveedor; el valor del ejemplo es ilustrativo.

### Respuesta ante errores de dependencias

Si un servicio dependiente responde con error, el agregador devuelve HTTP `502` o `503` y un JSON como el siguiente:

```json
{
  "status": "ERROR",
  "message": "A dependent service is unavailable."
}
```

## 4. Implementación y orquestación

El módulo `demo` implementa la API compuesta. `ProductSummaryService` configura un `WebClient` por servicio usando las URL base recibidas por configuración:

```java
this.catalogClient = WebClient.builder().baseUrl(catalogBaseUrl).build();
this.stockClient = WebClient.builder().baseUrl(stockBaseUrl).build();
this.exchangeClient = WebClient.builder().baseUrl(exchangeBaseUrl).build();
```

Cada cliente ejecuta una petición HTTP. Las tres respuestas se combinan de forma concurrente con `Mono.zip`:

```java
return Mono.zip(product, stock, exchangeRate)
        .map(results -> toSummary(
                productId,
                results.getT1(),
                results.getT2(),
                results.getT3()));
```

El método `toSummary` transforma `name` del catálogo a `title`, conserva los datos relevantes de stock y calcula el precio convertido. `ApiExceptionHandler` maneja errores HTTP y de conexión con los servicios dependientes.

### Configuración externa

Las URL base y el nombre de la fuente se configuran en `demo/src/main/resources/application.properties` y se pueden sobrescribir con variables de entorno:

| Variable | Valor predeterminado |
| --- | --- |
| `SERVER_PORT` | `8080` |
| `CATALOG_SERVICE_URL` | `http://localhost:8081` |
| `STOCK_SERVICE_URL` | `http://localhost:8082` |
| `EXCHANGE_SERVICE_URL` | `http://localhost:8083` |
| `EXCHANGE_SOURCE_NAME` | `Frankfurter External API` |

## 5. Ejecución local

Requiere Java 21 y Maven. Desde la raíz del repositorio, abre una terminal por servicio:

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
mvn clean spring-boot:run
```

Se recomienda `clean` al ejecutar `demo` si había clases compiladas de una versión anterior. Con los cuatro servicios en ejecución, consulta `http://localhost:8080/api/v1/product-summary/P-101` en Postman, Insomnia o con `curl`.

Los datos de ejemplo incluyen los productos `P-101` y `P-102`; el segundo no tiene unidades disponibles.

### Ejemplos de consultas individuales

```http
GET http://localhost:8081/microservices/products/P-101
GET http://localhost:8082/microservices/stock/P-101
GET http://localhost:8083/microservices/exchange?from=USD&to=MXN
```

## 6. Manejo de fallos

El agregador centraliza los fallos de sus dependencias. Una respuesta HTTP de error de un servicio dependiente se transforma en una respuesta de error del agregador; si la dependencia no acepta conexión, responde con HTTP `503`. El mensaje devuelto no expone detalles internos del proveedor.

## 7. Repositorio y flujo de trabajo

El trabajo del TSK-04 se encuentra en la rama `feat/api`. Los cambios deben integrarse mediante pull request siguiendo el flujo de ramas del repositorio.
