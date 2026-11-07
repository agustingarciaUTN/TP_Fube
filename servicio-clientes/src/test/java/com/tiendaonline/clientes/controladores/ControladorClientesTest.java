package com.tiendaonline.clientes.controladores;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiendaonline.clientes.DTOs.ClienteDTO;
import com.tiendaonline.clientes.DTOs.ItemPedidoResponseDTO;
import com.tiendaonline.clientes.DTOs.ListaPedidosResponseDTO;
import com.tiendaonline.clientes.DTOs.PedidoResponseDTO;
import com.tiendaonline.clientes.excepciones.*;
import com.tiendaonline.clientes.servicios.ServicioClientesImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ControladorClientes.class)
class ControladorClientesTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean 
    private ServicioClientesImpl servicioClientes;


    private ClienteDTO clienteDTOReq(){
        return ClienteDTO.builder()
                .nombre("Juan Perez")
                .email("juan@ejemplo.com")
                .build();
    }
    private ClienteDTO clienteDTORespuesta(){
        return ClienteDTO.builder()
                .id(UUID.fromString("a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d"))
                .nombre("Juan Perez")
                .email("juan@ejemplo.com")
                .fechaAlta(LocalDateTime.now())
                .build();
    }

    @Test
    void registrarCliente_Controlador_Correcto() throws Exception {
        ClienteDTO requestDto = clienteDTOReq();
        ClienteDTO respuestaDto = clienteDTORespuesta();

        given(servicioClientes.registrarCliente(any(ClienteDTO.class)))
                .willReturn(respuestaDto);

        mockMvc.perform(post("/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d"))
                .andExpect(jsonPath("$.nombre").value("Juan Perez"))
                .andExpect(jsonPath("$.email").value("juan@ejemplo.com"));
    }

    @Test
    void registrarCliente_Controlador_Fallo_EmailExistente() throws Exception {
        ClienteDTO requestDto = clienteDTOReq();

        given(servicioClientes.registrarCliente(any(ClienteDTO.class)))
                .willThrow(new EmailExistenteException("El correo electronico ya se encuentra registrado"));

        mockMvc.perform(post("/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codError").value(409))
                .andExpect(jsonPath("$.error").value("CONFLICT"))
                .andExpect(jsonPath("$.mensaje")
                        .value("El correo electronico ya se encuentra registrado"));
    }

    @Test
    void registrarCliente_Controlador_Fallo_NombreExistente() throws Exception {
        ClienteDTO requestDto = clienteDTOReq();

        given(servicioClientes.registrarCliente(any(ClienteDTO.class)))
                .willThrow(new NombreExistenteException("El nombre ya se encuentra registrado"));

        mockMvc.perform(post("/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codError").value(409))
                .andExpect(jsonPath("$.error").value("CONFLICT"))
                .andExpect(jsonPath("$.mensaje")
                        .value("El nombre ya se encuentra registrado"));
    }

    @Test
    void registrarCliente_Controlador_Fallo_NombreInvalido() throws Exception {
        ClienteDTO requestDto = ClienteDTO.builder()
                .nombre("").email("juan@ejemplo.com").build();

        given(servicioClientes.registrarCliente(any(ClienteDTO.class)))
                .willThrow(new NombreInvalidoException("El nombre no es valido"));

        mockMvc.perform(post("/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codError").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.mensaje")
                        .value("El nombre no es valido"));
    }

    @Test
    void registrarCliente_Controlador_Fallo_EmailInvalido() throws Exception {
        ClienteDTO requestDto = ClienteDTO.builder()
                .nombre("JuanPerez").email("juan@").build();

        given(servicioClientes.registrarCliente(any(ClienteDTO.class)))
                .willThrow(new EmailInvalidoException("El correo electronico no es valido"));

        mockMvc.perform(post("/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codError").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.mensaje")
                        .value("El correo electronico no es valido"));
    }

    @Test
    void consultarCliente_Controlador_Correcto_SinEnmascarar() throws Exception {
        ClienteDTO respuestaDto = clienteDTORespuesta();
        UUID idCliente = respuestaDto.getId();

        given(servicioClientes.consultarClientePorId(idCliente))
                .willReturn(respuestaDto);

        mockMvc.perform(get("/v1/clientes/{id}", idCliente)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(idCliente.toString()))
                .andExpect(jsonPath("$.nombre").value("Juan Perez"))
                .andExpect(jsonPath("$.email").value("juan@ejemplo.com"));
    }

    @Test
    void consultarCliente_Controlador_Correcto_Enmascarado() throws Exception {
        ClienteDTO respuestaDto = ClienteDTO.builder().id(UUID.fromString("a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d"))
                .nombre("J*** P***").email("****@****.com").build();
        UUID idCliente = respuestaDto.getId();

        given(servicioClientes.consultarClientePorId(idCliente))
                .willReturn(respuestaDto);

        mockMvc.perform(get("/v1/clientes/{id}", idCliente)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(idCliente.toString()))
                .andExpect(jsonPath("$.nombre").value("J*** P***"))
                .andExpect(jsonPath("$.email").value("****@****.com"));
    }

    @Test
    void consultarCliente_Controlador_Fallo_ClienteNoEncontrado() throws Exception {
        ClienteDTO respuestaDto = clienteDTORespuesta();
        UUID idCliente = respuestaDto.getId();

        given(servicioClientes.consultarClientePorId(idCliente))
                .willThrow(new ClienteNoEncontradoException("No se encontró el cliente con el id introducido"));

        mockMvc.perform(get("/v1/clientes/{id}", idCliente)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codError").value(404))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.mensaje")
                        .value("No se encontró el cliente con el id introducido"));
    }

    ListaPedidosResponseDTO ejListaPedidosResp(){
        return ListaPedidosResponseDTO.builder()
                .pedidos(ejListaPedido())
                .total(1)
                .build();
    }
    List<PedidoResponseDTO> ejListaPedido(){
        List<PedidoResponseDTO> lista = new ArrayList<>();
        PedidoResponseDTO pedido = PedidoResponseDTO.builder()
                .id( UUID.fromString("a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d"))
                .clienteId(UUID.fromString("35dc8345-858b-4153-98d4-7cf74eb99a5a"))
                .estado("CREADO")
                .montoTotal(BigDecimal.valueOf(55.5))
                .fechaCreacion(LocalDateTime.of(2026,8,10,12,4, 1))
                .fechaActualizacion(LocalDateTime.of(2026,9,12,14,4, 1))
                .items(ejItem())
                .build();
        lista.add(pedido);
        return lista;
    }
    List<ItemPedidoResponseDTO> ejItem(){
        List<ItemPedidoResponseDTO> lista = new ArrayList<>();
        ItemPedidoResponseDTO item = ItemPedidoResponseDTO.builder()
                .id(UUID.fromString("d9307190-9557-4372-b4a5-24a561e890ff"))
                .productoId(UUID.fromString("c2b1c4d4-e5d2-818b-0a0f-8f2a3a2c7c5a"))
                .cantidad(5)
                .precioUnitario(BigDecimal.valueOf(11.1))
                .build();
        lista.add(item);
        return lista;
    }

    @Test
    void listarPedidosCliente_Controlador_Correcto() throws Exception {
        ClienteDTO dto = clienteDTORespuesta();
        UUID idCliente = dto.getId();
        ListaPedidosResponseDTO respuestaPedidos = ejListaPedidosResp();
        PedidoResponseDTO pedido = respuestaPedidos.getPedidos().get(0);
        ItemPedidoResponseDTO item = pedido.getItems().get(0);

        given(servicioClientes.listarPedidosCliente(idCliente))
                .willReturn(respuestaPedidos);

        mockMvc.perform(get("/v1/clientes/{id}/pedidos", idCliente)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.pedidos").isArray())
                .andExpect(jsonPath("$.pedidos.length()").value(1))
                .andExpect(jsonPath("$.pedidos[0].id").value(pedido.getId().toString()))
                .andExpect(jsonPath("$.pedidos[0].clienteId").value(pedido.getClienteId().toString()))
                .andExpect(jsonPath("$.pedidos[0].estado").value(pedido.getEstado()))
                .andExpect(jsonPath("$.pedidos[0].montoTotal").value(pedido.getMontoTotal()))
                .andExpect(jsonPath("$.pedidos[0].fechaCreacion").value(pedido.getFechaCreacion().toString()))
                .andExpect(jsonPath("$.pedidos[0].fechaActualizacion").value(pedido.getFechaActualizacion().toString()))
                .andExpect(jsonPath("$.pedidos[0].items").isArray())
                .andExpect(jsonPath("$.pedidos[0].items.length()").value(1))
                .andExpect(jsonPath("$.pedidos[0].items[0].id").value(item.getId().toString()))
                .andExpect(jsonPath("$.pedidos[0].items[0].productoId").value(item.getProductoId().toString()))
                .andExpect(jsonPath("$.pedidos[0].items[0].cantidad").value(item.getCantidad()))
                .andExpect(jsonPath("$.pedidos[0].items[0].precioUnitario").value(item.getPrecioUnitario()));
    }

    @Test
    void listarPedidosCliente_Controlador_Fallo_ClienteNoEncontrado() throws Exception {
        ClienteDTO respuestaDto = clienteDTORespuesta();
        UUID idCliente = respuestaDto.getId();

        given(servicioClientes.listarPedidosCliente(idCliente))
                .willThrow(new ClienteNoEncontradoException("No se encontró el cliente con el id introducido"));

        mockMvc.perform(get("/v1/clientes/{id}/pedidos", idCliente)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codError").value(404))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.mensaje")
                        .value("No se encontró el cliente con el id introducido"));
    }

    @Test
    void listarPedidosCliente_Controlador_Fallo_ServicioExternoNoDisponible() throws Exception {
        ClienteDTO respuestaDto = clienteDTORespuesta();
        UUID idCliente = respuestaDto.getId();

        given(servicioClientes.listarPedidosCliente(idCliente))
                .willThrow(new ServicioExternoNoDisponibleException("El servicio de pedidos no está disponible"));

        mockMvc.perform(get("/v1/clientes/{id}/pedidos", idCliente)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.codError").value(503))
                .andExpect(jsonPath("$.error").value("SERVICE_UNAVAILABLE"))
                .andExpect(jsonPath("$.mensaje")
                        .value("El servicio de pedidos no está disponible"));
    }
}