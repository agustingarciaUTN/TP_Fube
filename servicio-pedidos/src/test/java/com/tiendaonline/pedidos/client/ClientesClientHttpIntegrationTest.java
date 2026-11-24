package com.tiendaonline.pedidos.client;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.verify.VerificationTimes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootTest
public class ClientesClientHttpIntegrationTest {
    static ClientAndServer mockServer = ClientAndServer.startClientAndServer();

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("servicios.clientes.url", () -> "http://localhost:" + mockServer.getPort());
    }

    @Autowired
    ClientesClient clientesClient;

    @BeforeEach
    void setup() {
        mockServer.reset();
    }

    @AfterAll
    static void stop() {
        mockServer.stop();
    }

    @Test
    public void clienteExistenteDevuelveTrue(){
        UUID id = UUID.randomUUID();

        mockServer
                .when(request()
                        .withMethod("GET").withPath("/v1/clientes/" + id))
                .respond(response()
                        .withStatusCode(200));

        boolean existe = clientesClient.clienteExiste(id);

        assertThat(existe).isTrue();

        mockServer.verify(request()
                .withMethod("GET").withPath("/v1/clientes/" + id), VerificationTimes.exactly(1));
    }

    @Test
    public void clienteInexistenteDevuelveFalse(){
        UUID id = UUID.randomUUID();

        mockServer
                .when(request()
                        .withMethod("GET").withPath("/v1/clientes/" + id))
                .respond(response()
                        .withStatusCode(404));

        boolean existe = clientesClient.clienteExiste(id);

        assertThat(existe).isFalse();

        mockServer.verify(request()
                .withMethod("GET").withPath("/v1/clientes/" + id), VerificationTimes.exactly(1));
    }

    @Test
    public void clientesLentoLanzaServicioNoDisponible(){
        UUID id = UUID.randomUUID();

        mockServer
                .when(request()
                        .withMethod("GET").withPath("/v1/clientes/" + id))
                .respond(response()
                        .withStatusCode(200).withDelay(TimeUnit.SECONDS, 5));

        assertThatThrownBy(() -> clientesClient.clienteExiste(id))
                .isInstanceOf(ServicioNoDisponibleException.class)
                .hasCauseInstanceOf(ResourceAccessException.class)
                .hasMessage("El servicio de clientes no esta disponible");
    }

    @Test
    public void clientesConErrorLanzaServicioNoDisponible(){
        UUID id = UUID.randomUUID();

        mockServer
                .when(request()
                        .withMethod("GET").withPath("/v1/clientes/" + id))
                .respond(response()
                        .withStatusCode(500));

        assertThatThrownBy(() -> clientesClient.clienteExiste(id))
                .isInstanceOf(ServicioNoDisponibleException.class)
                .hasCauseInstanceOf(HttpServerErrorException.class)
                .hasMessage("El servicio de clientes no esta disponible");
    }
}