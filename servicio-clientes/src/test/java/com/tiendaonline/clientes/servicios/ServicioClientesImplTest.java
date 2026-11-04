package com.tiendaonline.clientes.servicios;

import com.tiendaonline.clientes.DTOs.ClienteDTO;
import com.tiendaonline.clientes.DTOs.ItemPedidoResponseDTO;
import com.tiendaonline.clientes.DTOs.ListaPedidosResponseDTO;
import com.tiendaonline.clientes.DTOs.PedidoResponseDTO;
import com.tiendaonline.clientes.excepciones.*;
import com.tiendaonline.clientes.modelo.Cliente;
import com.tiendaonline.clientes.repositorios.RepositorioClientes;
import com.tiendaonline.clientes.statsig.GestorFeatureFlagImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServicioClientesImplTest {
    @Mock
    RepositorioClientes repositorioClientes;

    @Mock
    ConectorPedidosImpl servicioPedidos;

    @Mock
    GestorFeatureFlagImpl gestorFeatureFlag;

    @InjectMocks
    ServicioClientesImpl servicioClientes;

    private ClienteDTO ejClienteDTO() {
        return ClienteDTO.builder()
                .nombre("JuanPerez")
                .email("juanPerez@gmail.com")
                .build();
    }

    private Cliente ejClienteEntity() {
        return Cliente.builder()
                .nombre("JuanPerez")
                .email("juanPerez@gmail.com")
                .build();
    }

    @Test
    void registrarCliente_Correcto() {
        ClienteDTO dto = ejClienteDTO();
        given(repositorioClientes.existsByEmail(dto.getEmail()))
                .willReturn(false);
        given(repositorioClientes.existsByNombre(dto.getNombre()))
                .willReturn(false);
        given(repositorioClientes.save(any(Cliente.class)))
                .willAnswer(invocation -> invocation.getArgument(0));


        ClienteDTO resultado = servicioClientes.registrarCliente(dto);

        assertNotNull(resultado);
        assertEquals(dto.getNombre(), resultado.getNombre());
        assertEquals(dto.getEmail(), resultado.getEmail());

        ArgumentCaptor<Cliente> captor = ArgumentCaptor.forClass(Cliente.class);
        verify(repositorioClientes, times(1)).save(captor.capture());
        Cliente clienteGuardado = captor.getValue();

        assertEquals(dto.getNombre(), clienteGuardado.getNombre());
        assertEquals(dto.getEmail(), clienteGuardado.getEmail());
        assertNotNull(clienteGuardado.getFechaAlta());
    }

    @Test
    void registrarCliente_Fallo_EmailExistente() {
        ClienteDTO dto = ejClienteDTO();
        Cliente clienteEncontrado = ejClienteEntity();
        given(repositorioClientes.existsByEmail(dto.getEmail()))
                .willReturn(true);

        assertThrows(
                EmailExistenteException.class,
                () -> servicioClientes.registrarCliente(dto)
        );

        verify(repositorioClientes, never()).save(any(Cliente.class));
        verify(repositorioClientes, times(1))
                .existsByEmail(dto.getEmail());
    }

    @Test
    void registrarCliente_Fallo_NombreExistente(){
        ClienteDTO dto = ejClienteDTO();
        Cliente clienteEncontrado = ejClienteEntity();
        given(repositorioClientes.existsByEmail(dto.getEmail()))
                .willReturn(false);
        given(repositorioClientes.existsByNombre(dto.getNombre()))
                .willReturn(true);

        assertThrows(
                NombreExistenteException.class,
                () -> servicioClientes.registrarCliente(dto)
        );

        verify(repositorioClientes, never()).save(any(Cliente.class));
        verify(repositorioClientes, times(1))
                .existsByEmail(dto.getEmail());
        verify(repositorioClientes, times(1))
                .existsByNombre(dto.getNombre());
    }

    @ParameterizedTest //Prueba diferentes entradas que tienen que dar mismo error
    @NullSource //Si el nombre es null
    @ValueSource(strings = {
            "a", //Muy corto
            "Juan1", //Numero
            "Juan_Perez", //Simbolo
            "Juan/nPerez", //Salto de linea
            "", //Vacio
            " " //Solo espacio
    })
    void registrarCliente_Fallo_NombreInvalido(String nombre){
        ClienteDTO dto = ClienteDTO.builder().nombre(nombre).email("juanPerez@gmail.com").build();

        NombreInvalidoException exepcion = assertThrows(
                NombreInvalidoException.class,
                () -> servicioClientes.registrarCliente(dto)
        );

        assertEquals("El nombre no es valido", exepcion.getMessage());
        verify(repositorioClientes, never()).save(any(Cliente.class));
        verify(repositorioClientes, never())
                .existsByEmail(any(String.class));
        verify(repositorioClientes, never())
                .existsByNombre(any(String.class));
    }

    @ParameterizedTest //Prueba diferentes entradas que tienen que dar mismo error
    @NullSource //Si el nombre es null
    @ValueSource(strings = {
            "a.com", //Falta '@'
            "", //Vacio
            " ", //Solo espacio
            "@", //Solo '@'
            "a@b", //Falta ".[string]"
            "a@b.c9m", //Numero despues del punto
            "a^b@c.com", //Caracter no permitido ('^')
    })
    void registrarCliente_Fallo_EmailInvalido(String email){
        ClienteDTO dto = ClienteDTO.builder().nombre("juanPerez").email(email).build();

        EmailInvalidoException exepcion = assertThrows(
                EmailInvalidoException.class,
                () -> servicioClientes.registrarCliente(dto)
        );

        assertEquals("El correo electronico no es valido", exepcion.getMessage());
        verify(repositorioClientes, never()).save(any(Cliente.class));
        verify(repositorioClientes, never())
                .existsByEmail(any(String.class));
        verify(repositorioClientes, never())
                .existsByNombre(any(String.class));
    }

    @Test
    void consultarClientePorId_FlagFalse_Correcto() {
        Cliente cliente = ejClienteEntity();
        UUID id = UUID.fromString("35dc8345-858b-4153-98d4-7cf74eb99a5a");
        cliente.setId(id);

        given(repositorioClientes.findById(id)).willReturn(Optional.of(cliente));
        given(gestorFeatureFlag.isFlagActivo("mask-pii")).willReturn(false);

        ClienteDTO resultado = servicioClientes.consultarClientePorId(id);

        assertEquals("JuanPerez", resultado.getNombre());
        assertEquals("juanPerez@gmail.com", resultado.getEmail());
        verify(gestorFeatureFlag, times(1)).isFlagActivo("mask-pii");
    }
    @Test
    void consultarClientePorId_FlagTrue_Correcto() {
        Cliente cliente = ejClienteEntity();
        cliente.setNombre("Juan Perez");
        UUID id = UUID.fromString("35dc8345-858b-4153-98d4-7cf74eb99a5a");
        cliente.setId(id);

        given(repositorioClientes.findById(id)).willReturn(Optional.of(cliente));
        given(gestorFeatureFlag.isFlagActivo("mask-pii")).willReturn(true);

        ClienteDTO resultado = servicioClientes.consultarClientePorId(id);

        assertEquals("J*** P***", resultado.getNombre());
        assertEquals("****@****.com", resultado.getEmail());
        verify(gestorFeatureFlag, times(1)).isFlagActivo("mask-pii");
    }
    @Test
    void consultarClientePorId_IdInexistente() {
        Cliente cliente = ejClienteEntity();
        UUID id = UUID.fromString("35dc8345-858b-4153-98d4-7cf74eb99a5a");
        cliente.setId(id);

        given(repositorioClientes.findById(id)).willReturn(Optional.empty());
        ClienteNoEncontradoException exepcion = assertThrows(
                ClienteNoEncontradoException.class,
                () -> servicioClientes.consultarClientePorId(id)
        );

        verify(gestorFeatureFlag, never()).isFlagActivo("mask-pii");
        assertEquals("No se encontró el cliente con el id introducido", exepcion.getMessage());
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
                .fechaCreacion(LocalDateTime.of(2026,8,10,12,4))
                .fechaActualizacion(LocalDateTime.of(2026,9,12,14,4))
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
    void listarPedidosCliente_Correcto(){
        ClienteDTO dto = ejClienteDTO();
        UUID idCliente = dto.getId();
        ListaPedidosResponseDTO respuestaPedidos = ejListaPedidosResp();
        given(servicioPedidos.listarPedidosCliente(idCliente)).willReturn(respuestaPedidos);

        ListaPedidosResponseDTO resultado = servicioClientes.listarPedidosCliente(idCliente);

        assertNotNull(resultado);
        assertEquals(respuestaPedidos, resultado);
    }

    @Test
    void listarPedidosCliente_Fallo_clienteNoEncontrado(){
        ClienteDTO dto = ejClienteDTO();
        UUID idCliente = dto.getId();
        ListaPedidosResponseDTO respuestaPedidos = ejListaPedidosResp();
        given(servicioPedidos.listarPedidosCliente(idCliente)).willThrow(
                new ClienteNoEncontradoException("No se encontró el cliente con el id introducido")
        );

        ClienteNoEncontradoException excepcion = assertThrows(
                ClienteNoEncontradoException.class,
                () -> servicioClientes.listarPedidosCliente(idCliente)
        );

        verify(servicioPedidos, times(1)).listarPedidosCliente(idCliente);
        assertEquals("No se encontró el cliente con el id introducido", excepcion.getMessage());
    }

    @Test
    void listarPedidosCliente_Fallo_FalloServicioPedidos(){
        ClienteDTO dto = ejClienteDTO();
        UUID idCliente = dto.getId();
        ListaPedidosResponseDTO respuestaPedidos = ejListaPedidosResp();
        given(servicioPedidos.listarPedidosCliente(idCliente)).willThrow(
                new ServicioExternoNoDisponibleException("Ocurrió un error inesperado con el servicio de pedidos")
        );

        ServicioExternoNoDisponibleException excepcion = assertThrows(
                ServicioExternoNoDisponibleException.class,
                () -> servicioClientes.listarPedidosCliente(idCliente)
        );

        verify(servicioPedidos, times(1)).listarPedidosCliente(idCliente);
        assertEquals("Ocurrió un error inesperado con el servicio de pedidos", excepcion.getMessage());
    }
}
