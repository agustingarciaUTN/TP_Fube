package com.tiendaonline.clientes.servicios;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiendaonline.clientes.DTOs.ItemPedidoResponseDTO;
import com.tiendaonline.clientes.DTOs.ListaPedidosResponseDTO;
import com.tiendaonline.clientes.DTOs.PedidoResponseDTO;
import com.tiendaonline.clientes.excepciones.ClienteNoEncontradoException;
import com.tiendaonline.clientes.excepciones.ServicioExternoNoDisponibleException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(ConectorPedidosImpl.class)
@TestPropertySource(properties = "servicios.pedidos.url=http://localhost:8083")
class ConectorPedidosImplTest {
    @Autowired
    private ConectorPedidosImpl conectorPedidos;
    @Autowired
    private MockRestServiceServer mockServer;
    @Autowired
    private ObjectMapper objectMapper;

    ItemPedidoResponseDTO ejItem(){
        return ItemPedidoResponseDTO.builder()
                .id(UUID.fromString("d9307190-9557-4372-b4a5-24a561e890ff"))
                .productoId(UUID.fromString("a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d"))
                .cantidad(2)
                .precioUnitario(BigDecimal.valueOf(100.0))
                .build();
    }
    PedidoResponseDTO ejPedido(){
        return PedidoResponseDTO.builder()
                .id(UUID.fromString("c2b1c4d4-e5d2-818b-0a0f-8f2a3a2c7c5a"))
                .clienteId(UUID.fromString("35dc8345-858b-4153-98d4-7cf74eb99a5a"))
                .estado("CREADO")
                .montoTotal(BigDecimal.valueOf(200.0))
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .items(List.of(ejItem()))
                .build();
    }

    ListaPedidosResponseDTO ejLista(){
        return ListaPedidosResponseDTO.builder()
                .pedidos(List.of(ejPedido()))
                .total(1)
                .build();
    }



    @Test
    void listarPedidosCliente_Correcto() throws Exception{
        UUID clienteId = UUID.fromString("35dc8345-858b-4153-98d4-7cf74eb99a5a");

        ListaPedidosResponseDTO respuestaEsperada = ejLista();

        mockServer.expect(requestTo("http://localhost:8083/v1/pedidos?clienteId=" + clienteId))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(objectMapper.writeValueAsString(respuestaEsperada), MediaType.APPLICATION_JSON));

        ListaPedidosResponseDTO resultado = conectorPedidos.listarPedidosCliente(clienteId);

        assertNotNull(resultado);
        assertEquals(1, resultado.total());
        assertEquals(clienteId, resultado.getPedidos().get(0).getClienteId());
        mockServer.verify();
    }

    @Test
    void listarPedidosCliente_Error_ClienteNoEncontrado() {
        UUID clienteId = UUID.fromString("35dc8345-858b-4153-98d4-7cf74eb99a5a");

        mockServer.expect(requestTo("http://localhost:8083/v1/pedidos?clienteId=" + clienteId))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThrows(ClienteNoEncontradoException.class, () -> {
            conectorPedidos.listarPedidosCliente(clienteId);
        });

        mockServer.verify();
    }

    @Test
    void listarPedidosCliente_Error_ServicioExternoNoDisponible() {
        UUID clienteId = UUID.randomUUID();

        mockServer.expect(requestTo("http://localhost:8083/v1/pedidos?clienteId=" + clienteId))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        ServicioExternoNoDisponibleException ex = assertThrows(ServicioExternoNoDisponibleException.class, () -> {
            conectorPedidos.listarPedidosCliente(clienteId);
        });

        assertEquals("El servicio de pedidos no está disponible", ex.getMessage());
        mockServer.verify();
    }
}