package com.tiendaonline.pedidos.client;

import com.tiendaonline.pedidos.dominio.ProductoNoEncontradoException;
import com.tiendaonline.pedidos.dominio.StockInsuficienteException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@Slf4j
public class InventarioClientHttp implements InventarioClient {

    private final RestClient restClient;

    public InventarioClientHttp(@Qualifier("inventarioRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public ReservaConfirmada reservar(UUID productoId, int cantidad, UUID pedidoId) {
        try {
            BigDecimal precio = restClient.get()
                    .uri("/v1/productos/{id}/precio", productoId)
                    .retrieve().body(BigDecimal.class);

            if (precio == null) {
                throw new ServicioNoDisponibleException("Precio no encontrado");
            }

            RespuestaReserva respuesta = restClient.post()
                    .uri("/v1/reservas")
                    .body(new SolicitudReserva(productoId, pedidoId, cantidad))
                    .retrieve().body(RespuestaReserva.class);

            if (respuesta == null) {
                throw new ServicioNoDisponibleException("Inventario no devolvio la reserva del producto " + productoId);
            }

            return new ReservaConfirmada(respuesta.id(), precio);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ProductoNoEncontradoException("Producto " + productoId + " no encontrado");
        } catch (HttpClientErrorException.Conflict e) {
            throw new StockInsuficienteException("No se puede realizar la reserva del producto " + productoId);
        } catch (HttpServerErrorException | ResourceAccessException e) {
            throw new ServicioNoDisponibleException("El servicio de inventario no esta disponible", e);
        }

    }

    @Override
    public void liberar(UUID reservaId) {
        try {
            restClient.patch()
                    .uri("/v1/reservas/{reservaId}/liberacion", reservaId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.Conflict e) {
            log.info("La reserva {} ya estaba liberada", reservaId);
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("La reserva {} no existe", reservaId);
        } catch (HttpServerErrorException | ResourceAccessException e) {
            throw new ServicioNoDisponibleException("No se pudo liberar la reserva " + reservaId, e);
        }
    }
}
