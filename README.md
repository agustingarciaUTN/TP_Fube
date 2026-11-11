# TP Fundamentos del Desarrollo Backend

Trabajo práctico final de Fundamentos de Desarrollo Backend (UTN FRSF, 2026).

La idea es modelar una tienda online con tres microservicios independientes en Spring Boot, cada
uno con su propia base de datos. Clientes registra y consulta clientes, inventario maneja el stock
y las reservas de productos, y pedidos crea y gestiona los pedidos comunicándose con los otros dos.
El enunciado completo está en [`docs/TP 2026.pdf`](./docs/TP%202026.pdf).

## Servicios y puertos

| Servicio             | Puerto app | Base de datos | Puerto base |
|----------------------|------------|---------------|-------------|
| servicio-clientes    | 8081       | clientes_db   | 5432        |
| servicio-inventario  | 8082       | inventario_db | 5433        |
| servicio-pedidos     | 8083       | pedidos_db    | 5434        |

## Requisitos

- JDK 17
- Docker Desktop

## Cómo levantarlo

Primero se crea el `.env` a partir de la plantilla y se levantan las bases.

```bash
cp .env.example .env
docker compose up -d
```

Servicio-clientes evalúa el feature flag `mask-pii` con Statsig, así que necesita la Server Secret
Key de la consola de Statsig en una variable de entorno.

```powershell
$env:STATSIG_SECRET_KEY="<server-secret-key>"
```

Después se corre cada servicio en una terminal distinta.

```bash
./gradlew :servicio-clientes:bootRun
./gradlew :servicio-inventario:bootRun
./gradlew :servicio-pedidos:bootRun
```

## Tests

Con las bases levantadas se corren todos los tests desde la raíz, o los de un solo servicio.

```bash
./gradlew test
./gradlew :servicio-pedidos:test
```

Los tests de integración simulan los otros servicios con MockServer, por lo que no hace falta
tenerlos corriendo.

## Endpoints

Con un servicio corriendo, su documentación OpenAPI está en `/swagger-ui.html`
(por ejemplo http://localhost:8083/swagger-ui.html).

| Servicio   | Método | Ruta                             | Descripción                                 |
|------------|--------|----------------------------------|---------------------------------------------|
| clientes   | POST   | `/v1/clientes`                   | Registrar un cliente                        |
| clientes   | GET    | `/v1/clientes/{id}`              | Consultar un cliente (PII según `mask-pii`) |
| clientes   | GET    | `/v1/clientes/{id}/pedidos`      | Pedidos del cliente                         |
| inventario | GET    | `/v1/productos/{id}/stock`       | Stock disponible de un producto             |
| inventario | GET    | `/v1/productos/{id}/precio`      | Precio unitario de un producto              |
| inventario | POST   | `/v1/reservas`                   | Reservar unidades para un pedido            |
| inventario | PATCH  | `/v1/reservas/{id}/liberacion`   | Liberar una reserva                         |
| pedidos    | GET    | `/v1/pedidos/{id}`               | Consultar un pedido                         |
| pedidos    | GET    | `/v1/pedidos?clienteId={id}`     | Listar los pedidos de un cliente            |
