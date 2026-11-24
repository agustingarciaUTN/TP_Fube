package com.tiendaonline.pedidos.client;

import com.tiendaonline.pedidos.dominio.ProductoNoEncontradoException;
import com.tiendaonline.pedidos.dominio.StockInsuficienteException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.MediaType;
import org.mockserver.verify.VerificationTimes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.HttpServerErrorException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.JsonBody.json;

@SpringBootTest
public class InventarioClientHttpIntegrationTest {

    static ClientAndServer mockServer = ClientAndServer.startClientAndServer();

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("servicios.inventario.url", () -> "http://localhost:" + mockServer.getPort());
    }

    @Autowired
    InventarioClient inventarioClient;

    @BeforeEach
    void setup() {
        mockServer.reset();
    }

    @AfterAll
    static void stop() {
        mockServer.stop();
    }

    @Test
    public void reservaExitosaDevuelveReservaConPrecio(){
        UUID productoId = UUID.randomUUID();
        UUID pedidoId = UUID.randomUUID();
        UUID reservaId = UUID.randomUUID();
        int cantidad = 3;

        mockServer
                .when(request()
                        .withMethod("GET").withPath("/v1/productos/" + productoId + "/precio"))
                .respond(response()
                        .withStatusCode(200)
                        .withBody("1500.50", MediaType.APPLICATION_JSON));

        mockServer
                .when(request()
                        .withMethod("POST").withPath("/v1/reservas"))
                .respond(response()
                        .withStatusCode(201)
                        .withBody("{\"id\":\"" + reservaId + "\"}",  MediaType.APPLICATION_JSON));

        ReservaConfirmada reservaHecha = inventarioClient.reservar(productoId, cantidad,  pedidoId);

        assertThat(reservaHecha.reservaId()).isEqualTo(reservaId);
        assertThat(reservaHecha.precioUnitario()).isEqualByComparingTo("1500.50");

        mockServer.verify(request()
                .withMethod("POST").withPath("/v1/reservas").withBody(
                        json("{\"pedidoId\":\"" + pedidoId + "\","
                                + "\"productoId\":\"" + productoId + "\","
                                + "\"cantidad\":" + cantidad + "}")),  VerificationTimes.exactly(1));
    }

    @Test
    public void productoInexistenteLanzaProductoNoEncontrado(){
        UUID productoId = UUID.randomUUID();
        UUID pedidoId = UUID.randomUUID();
        int cantidad = 3;

        mockServer
                .when(request()
                        .withMethod("GET").withPath("/v1/productos/" + productoId + "/precio"))
                .respond(response()
                        .withStatusCode(404));

        assertThatThrownBy(() -> inventarioClient.reservar(productoId, cantidad,  pedidoId))
                .isInstanceOf(ProductoNoEncontradoException.class)
                .hasMessage("Producto " + productoId + " no encontrado");

        mockServer.verify(request()
                .withMethod("GET").withPath("/v1/productos/" + productoId + "/precio"),  VerificationTimes.exactly(1));
        mockServer.verify(request()
                .withMethod("POST").withPath("/v1/reservas"),  VerificationTimes.never());
    }


    @Test
    public void stockInsuficienteLanzaStockInsuficiente(){
        UUID productoId = UUID.randomUUID();
        UUID pedidoId = UUID.randomUUID();
        int cantidad = 3;

        mockServer
                .when(request()
                        .withMethod("GET").withPath("/v1/productos/" + productoId + "/precio"))
                .respond(response()
                        .withStatusCode(200)
                        .withBody("1500.50", MediaType.APPLICATION_JSON));
        mockServer
                .when(request()
                        .withMethod("POST").withPath("/v1/reservas"))
                .respond(response()
                        .withStatusCode(409));

        assertThatThrownBy(() -> inventarioClient.reservar(productoId, cantidad,  pedidoId))
                .isInstanceOf(StockInsuficienteException.class)
                .hasMessage("No se puede realizar la reserva del producto " + productoId);

        mockServer.verify(request()
                .withMethod("POST").withPath("/v1/reservas"),  VerificationTimes.exactly(1));
    }

    @Test
    public void inventarioConErrorAlReservarLanzaServicioNoDisponible(){
        UUID productoId = UUID.randomUUID();
        UUID pedidoId = UUID.randomUUID();
        int cantidad = 3;

        mockServer
                .when(request()
                        .withMethod("GET").withPath("/v1/productos/" + productoId + "/precio"))
                .respond(response()
                        .withStatusCode(500));

        assertThatThrownBy(() -> inventarioClient.reservar(productoId, cantidad,  pedidoId))
                .isInstanceOf(ServicioNoDisponibleException.class)
                .hasCauseInstanceOf(HttpServerErrorException.class)
                .hasMessage("El servicio de inventario no esta disponible");

        mockServer.verify(request()
                .withMethod("POST").withPath("/v1/reservas"),  VerificationTimes.never());
    }

    @Test
    public void liberarReservaExistenteNoLanzaExcepcion(){
        UUID reservaId = UUID.randomUUID();

        mockServer
                .when(request()
                        .withMethod("PATCH").withPath("/v1/reservas/" + reservaId +"/liberacion"))
                .respond(response()
                        .withStatusCode(200));

        assertThatCode(() -> inventarioClient.liberar(reservaId)).doesNotThrowAnyException();

        mockServer.verify(request()
                .withMethod("PATCH").withPath("/v1/reservas/" + reservaId +"/liberacion"),  VerificationTimes.exactly(1));
    }

    @Test
    public void liberarReservaYaLiberadaNoLanzaExcepcion(){
        UUID reservaId = UUID.randomUUID();

        mockServer
                .when(request()
                        .withMethod("PATCH").withPath("/v1/reservas/" + reservaId +"/liberacion"))
                .respond(response()
                        .withStatusCode(409));

        assertThatCode(() -> inventarioClient.liberar(reservaId)).doesNotThrowAnyException();

        mockServer.verify(request()
                .withMethod("PATCH").withPath("/v1/reservas/" + reservaId +"/liberacion"),  VerificationTimes.exactly(1));
    }

    @Test
    public void inventarioConErrorAlLiberarLanzaServicioNoDisponible(){
        UUID reservaId =  UUID.randomUUID();

        mockServer
                .when(request()
                        .withMethod("PATCH").withPath("/v1/reservas/" + reservaId +"/liberacion"))
                .respond(response()
                        .withStatusCode(500));

        assertThatThrownBy(() -> inventarioClient.liberar(reservaId))
                .isInstanceOf(ServicioNoDisponibleException.class)
                .hasCauseInstanceOf(HttpServerErrorException.class)
                .hasMessage("No se pudo liberar la reserva " + reservaId);
    }
}