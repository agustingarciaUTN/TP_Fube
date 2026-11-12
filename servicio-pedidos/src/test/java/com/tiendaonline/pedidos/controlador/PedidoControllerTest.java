package com.tiendaonline.pedidos.controlador;

import com.tiendaonline.pedidos.dominio.ItemPedido;
import com.tiendaonline.pedidos.dominio.Pedido;
import com.tiendaonline.pedidos.dominio.PedidoNoEncontradoException;
import com.tiendaonline.pedidos.dominio.StockInsuficienteException;
import com.tiendaonline.pedidos.dto.CrearPedidoRequest;
import com.tiendaonline.pedidos.dto.ListaPedidosResponse;
import com.tiendaonline.pedidos.dto.PedidoResponse;
import com.tiendaonline.pedidos.servicio.PedidoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PedidoController.class)
public class PedidoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PedidoService pedidoService;

    private Pedido pedidoValido(UUID clienteId) {
        ItemPedido itemPedido = new ItemPedido(2, new BigDecimal("10.50"), UUID.randomUUID(), UUID.randomUUID());
        ItemPedido itemPedido2 = new ItemPedido(3, new BigDecimal("10.50"), UUID.randomUUID(), UUID.randomUUID());

        return new Pedido(clienteId, List.of(itemPedido, itemPedido2));
    }

    private String jsonPedido(UUID clienteId, UUID productoId) {
        return "{\"clienteId\":\"" + clienteId + "\","
                + "\"items\":[{\"productoId\":\"" + productoId + "\",\"cantidad\":2}]}";
    }

    @Test
    void obtenerPedidoExistenteDevuelve200() throws Exception {
        Pedido pedido = pedidoValido(UUID.randomUUID());
        when(pedidoService.obtener(pedido.getId())).thenReturn(PedidoResponse.desde(pedido));

        mockMvc.perform(get("/v1/pedidos/{id}", pedido.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(pedido.getId().toString()))
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.estado").value("CREADO"));
    }

    @Test
    void obtenerPedidoInexistenteDevuelve404() throws Exception {
        UUID id = UUID.randomUUID();
        when(pedidoService.obtener(id)).thenThrow(new PedidoNoEncontradoException("El pedido indicado no existe"));

        mockMvc.perform(get("/v1/pedidos/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("El pedido indicado no existe"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Pedido no encontrado"));
    }

    @Test
    void obtenerConIdInvalidoDevuelve400() throws Exception {
        mockMvc.perform(get("/v1/pedidos/abc")).andExpect(status().isBadRequest());
    }

    @Test
    void listarPorClienteDevuelve200() throws Exception {
        UUID clienteId = UUID.randomUUID();
        Pedido pedido = pedidoValido(clienteId);
        Pedido pedido2 = pedidoValido(clienteId);
        when(pedidoService.listarPorCliente(clienteId)).thenReturn(ListaPedidosResponse.desde(List.of(pedido, pedido2)));

        mockMvc.perform(get("/v1/pedidos").param("clienteId", clienteId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(2))
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.items[0].clienteId").value(clienteId.toString()));
    }

    @Test
    void listarSinClienteIdDevuelve400() throws Exception {
        mockMvc.perform(get("/v1/pedidos")).andExpect(status().isBadRequest());
    }

    @Test
    void crearPedidoDevuelve201ConLocation() throws Exception {
        UUID clienteId = UUID.randomUUID();
        Pedido pedido = pedidoValido(clienteId);

        when(pedidoService.crear(any(CrearPedidoRequest.class)))
                .thenReturn(PedidoResponse.desde(pedido));

        mockMvc.perform(post("/v1/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPedido(clienteId, UUID.randomUUID())))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/v1/pedidos/" + pedido.getId())));
    }

    @Test
    void crearPedidoSinItemsDevuelve400() throws Exception {
        UUID clienteId = UUID.randomUUID();

        mockMvc.perform(post("/v1/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clienteId\":\"" + clienteId + "\","
                                + "\"items\":[]}"))
                .andExpect(status().isBadRequest());

        verify(pedidoService, never()).crear(any(CrearPedidoRequest.class));
    }

    @Test
    void crearPedidoSinStockDevuelve409() throws Exception {
        when(pedidoService.crear(any(CrearPedidoRequest.class)))
                .thenThrow(new StockInsuficienteException("Stock insuficiente"));

        mockMvc.perform(post("/v1/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPedido(UUID.randomUUID(), UUID.randomUUID())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Stock insuficiente"));
    }
}
