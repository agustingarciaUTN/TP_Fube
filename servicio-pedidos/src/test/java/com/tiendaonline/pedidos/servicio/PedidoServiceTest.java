package com.tiendaonline.pedidos.servicio;

import com.tiendaonline.pedidos.client.ClientesClient;
import com.tiendaonline.pedidos.client.InventarioClient;
import com.tiendaonline.pedidos.client.ReservaConfirmada;
import com.tiendaonline.pedidos.client.ServicioNoDisponibleException;
import com.tiendaonline.pedidos.dominio.*;
import com.tiendaonline.pedidos.dto.CrearPedidoRequest;
import com.tiendaonline.pedidos.dto.ItemPedidoRequest;
import com.tiendaonline.pedidos.dto.ListaPedidosResponse;
import com.tiendaonline.pedidos.dto.PedidoResponse;
import com.tiendaonline.pedidos.repositorio.PedidoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PedidoServiceTest {
    @Mock
    private PedidoRepository pedidoRepositorio;

    @Mock
    private ClientesClient clientesClient;

    @Mock
    private InventarioClient inventarioClient;

    @InjectMocks
    private PedidoService servicio;

    private Pedido pedidoValido(UUID clienteId) {
        ItemPedido itemPedido = new ItemPedido(2, new BigDecimal("10.50"), UUID.randomUUID(), UUID.randomUUID());
        ItemPedido itemPedido2 = new ItemPedido(3, new BigDecimal("10.50"), UUID.randomUUID(), UUID.randomUUID());

        return new Pedido(clienteId, List.of(itemPedido, itemPedido2));
    }

    @Test
    void obtenerPedidoExistenteDevuelveSuRespuesta() {
        UUID clienteId = UUID.randomUUID();
        Pedido pedido = pedidoValido(clienteId);

        when(pedidoRepositorio.findById(pedido.getId())).thenReturn(Optional.of(pedido));

        PedidoResponse respuesta = servicio.obtener(pedido.getId());

        assertThat(respuesta).isNotNull();
        assertThat(respuesta.id()).isEqualTo(pedido.getId());
        assertThat(respuesta.clienteId()).isEqualTo(clienteId);
        assertThat(respuesta.estado()).isEqualTo(EstadoPedido.CREADO);
        assertThat(respuesta.montoTotal()).isEqualByComparingTo(new BigDecimal("52.50"));
        assertThat(respuesta.items()).hasSize(2);
    }

    @Test
    void obtenerPedidoInexistenteLanzaExcepcion() {
        UUID pedidoId = UUID.randomUUID();
        when(pedidoRepositorio.findById(pedidoId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicio.obtener(pedidoId))
                .isInstanceOf(PedidoNoEncontradoException.class)
                .hasMessage("El pedido indicado no existe");
    }

    @Test
    void listarPorClienteDevuelveSusPedidos(){
        UUID clienteId = UUID.randomUUID();
        Pedido pedido = pedidoValido(clienteId);
        Pedido pedido2 = pedidoValido(clienteId);
        List<Pedido> pedidos = List.of(pedido, pedido2);

        when(pedidoRepositorio.findByClienteId(clienteId)).thenReturn(pedidos);

        ListaPedidosResponse respuesta = servicio.listarPorCliente(clienteId);

        assertThat(respuesta).isNotNull();
        assertThat(respuesta.total()).isEqualTo(2);
        assertThat(respuesta.items()).hasSize(2);
        assertThat(respuesta.items()).extracting(PedidoResponse::clienteId).containsOnly(clienteId);
    }

    @Test
    void listarUnClienteSinPedidosDevuelveListaVacia(){
        UUID clienteId = UUID.randomUUID();

        when(pedidoRepositorio.findByClienteId(clienteId)).thenReturn(List.of());

        ListaPedidosResponse respuesta = servicio.listarPorCliente(clienteId);

        assertThat(respuesta.total()).isZero();
        assertThat(respuesta.items()).isEmpty();
    }

    @Test
    public void crearPedidoConDosItemsReservaCadaUnoYCalculaElTotal(){
        UUID clienteId = UUID.randomUUID();
        UUID productoAId = UUID.randomUUID();
        UUID productoBId = UUID.randomUUID();
        UUID reservaAId = UUID.randomUUID();
        UUID reservaBId = UUID.randomUUID();
        ItemPedidoRequest itemPedidoRequest = new ItemPedidoRequest(productoAId, 2);
        ItemPedidoRequest itemPedidoRequest2 = new ItemPedidoRequest(productoBId, 3);

        when(clientesClient.clienteExiste(clienteId)).thenReturn(true);
        when(inventarioClient.reservar(eq(productoAId), eq(2), any(UUID.class)))
                .thenReturn(new ReservaConfirmada(reservaAId, new BigDecimal("100")));
        when(inventarioClient.reservar(eq(productoBId), eq(3), any(UUID.class)))
                .thenReturn(new ReservaConfirmada(reservaBId, new BigDecimal("200")));

        CrearPedidoRequest pedidoRequest = new  CrearPedidoRequest(clienteId, List.of(itemPedidoRequest, itemPedidoRequest2));

        PedidoResponse respuesta = servicio.crear(pedidoRequest);

        assertThat(respuesta).isNotNull();
        assertThat(respuesta.montoTotal()).isEqualByComparingTo(new BigDecimal("800"));
        assertThat(respuesta.estado()).isEqualTo(EstadoPedido.CREADO);
        assertThat(respuesta.items()).hasSize(2);

        verify(pedidoRepositorio).save(any(Pedido.class));
    }

    @Test
    public void crearPedidoConClienteInexistenteLanzaExcepcionYNoReserva(){
        UUID clienteId = UUID.randomUUID();
        ItemPedidoRequest itemPedidoRequest = new ItemPedidoRequest(UUID.randomUUID(), 2);
        CrearPedidoRequest pedidoRequest = new  CrearPedidoRequest(clienteId, List.of(itemPedidoRequest));

        when(clientesClient.clienteExiste(clienteId)).thenReturn(false);

        assertThatThrownBy(() -> servicio.crear(pedidoRequest))
                .isInstanceOf(ClienteNoEncontradoException.class)
                .hasMessage("El cliente " + clienteId + " no existe");

        verifyNoInteractions(inventarioClient);
        verify(pedidoRepositorio, never()).save(any(Pedido.class));
    }

    @Test
    public void sinStockEnElPrimerItemNoGuardaNiLibera(){
        UUID clienteId = UUID.randomUUID();
        UUID productoAId = UUID.randomUUID();
        ItemPedidoRequest itemPedidoRequest = new ItemPedidoRequest(productoAId, 2);
        ItemPedidoRequest itemPedidoRequest2 = new ItemPedidoRequest(UUID.randomUUID(), 3);
        CrearPedidoRequest pedidoRequest = new  CrearPedidoRequest(clienteId, List.of(itemPedidoRequest, itemPedidoRequest2));

        when(clientesClient.clienteExiste(clienteId)).thenReturn(true);
        when(inventarioClient.reservar(eq(productoAId), eq(2), any(UUID.class)))
                .thenThrow(StockInsuficienteException.class);

        assertThatThrownBy(() -> servicio.crear(pedidoRequest))
                .isInstanceOf(StockInsuficienteException.class);

        verify(inventarioClient, never()).liberar(any(UUID.class));
        verify(pedidoRepositorio, never()).save(any(Pedido.class));
    }

    @Test
    public void sinStockEnElSegundoItemLiberaLaReservaDelPrimero() {
        UUID clienteId = UUID.randomUUID();
        UUID productoAId = UUID.randomUUID();
        UUID productoBId = UUID.randomUUID();
        UUID reservaAId = UUID.randomUUID();
        ItemPedidoRequest itemPedidoRequest = new ItemPedidoRequest(productoAId, 2);
        ItemPedidoRequest itemPedidoRequest2 = new ItemPedidoRequest(productoBId, 3);
        CrearPedidoRequest pedidoRequest = new  CrearPedidoRequest(clienteId, List.of(itemPedidoRequest, itemPedidoRequest2));

        when(clientesClient.clienteExiste(clienteId)).thenReturn(true);
        when(inventarioClient.reservar(eq(productoAId), eq(2), any(UUID.class)))
                .thenReturn(new ReservaConfirmada(reservaAId, new BigDecimal("100")));
        when(inventarioClient.reservar(eq(productoBId), eq(3), any(UUID.class)))
                .thenThrow(StockInsuficienteException.class);


        assertThatThrownBy(() -> servicio.crear(pedidoRequest))
                .isInstanceOf(StockInsuficienteException.class);

        verify(inventarioClient).liberar(reservaAId);
        verify(pedidoRepositorio, never()).save(any(Pedido.class));
    }

    @Test
    public void inventarioCaidoAMitadDelPedidoLiberaLoReservado(){
        UUID clienteId = UUID.randomUUID();
        UUID productoAId = UUID.randomUUID();
        UUID productoBId = UUID.randomUUID();
        UUID reservaAId = UUID.randomUUID();
        ItemPedidoRequest itemPedidoRequest = new ItemPedidoRequest(productoAId, 2);
        ItemPedidoRequest itemPedidoRequest2 = new ItemPedidoRequest(productoBId, 3);
        CrearPedidoRequest pedidoRequest = new  CrearPedidoRequest(clienteId, List.of(itemPedidoRequest, itemPedidoRequest2));

        String mensajeErrorReservar = "El servicio de inventarios se encontro caido al reservar";

        when(clientesClient.clienteExiste(clienteId)).thenReturn(true);
        when(inventarioClient.reservar(eq(productoAId), eq(2), any(UUID.class)))
                .thenReturn(new ReservaConfirmada(reservaAId, new BigDecimal("100")));
        when(inventarioClient.reservar(eq(productoBId), eq(3), any(UUID.class)))
                .thenThrow(new ServicioNoDisponibleException(mensajeErrorReservar));
        doThrow(new ServicioNoDisponibleException("El servicio de inventarios se encontro caido al liberar"))
                .when(inventarioClient).liberar(any(UUID.class));

        assertThatThrownBy(() -> servicio.crear(pedidoRequest))
                .isInstanceOf(ServicioNoDisponibleException.class)
                .hasMessage(mensajeErrorReservar);

        verify(inventarioClient).liberar(reservaAId);
        verify(pedidoRepositorio, never()).save(any(Pedido.class));
    }

    @Test
    void cancelarPedidoCreadoLiberaReservasYQuedaCancelado(){
        UUID clienteId = UUID.randomUUID();
        Pedido pedido = pedidoValido(clienteId);

        when(pedidoRepositorio.findById(pedido.getId())).thenReturn(Optional.of(pedido));

        PedidoResponse respuesta = servicio.cambiarEstado(pedido.getId(), EstadoPedido.CANCELADO);

        assertThat(respuesta.estado()).isEqualTo(EstadoPedido.CANCELADO);
        verify(inventarioClient).liberar(pedido.getItems().get(0).getReservaId());
        verify(inventarioClient).liberar(pedido.getItems().get(1).getReservaId());
        verify(pedidoRepositorio).save(pedido);
    }

    @Test
    void cancelarPedidoInexistenteLanzaExcepcion(){
        UUID id = UUID.randomUUID();

        when(pedidoRepositorio.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicio.cambiarEstado(id, EstadoPedido.CANCELADO))
                .isInstanceOf(PedidoNoEncontradoException.class);
        verifyNoInteractions(inventarioClient);
        verify(pedidoRepositorio, never()).save(any(Pedido.class));
    }

    @Test
    void cancelarPedidoYaCanceladoLanzaTransicionInvalida(){
        UUID clienteId = UUID.randomUUID();
        Pedido pedido = pedidoValido(clienteId);

        pedido.cambiarEstado(EstadoPedido.CANCELADO);
        when(pedidoRepositorio.findById(pedido.getId())).thenReturn(Optional.of(pedido));

        assertThatThrownBy(() -> servicio.cambiarEstado(pedido.getId(), EstadoPedido.CANCELADO))
                .isInstanceOf(TransicionInvalidaException.class);

        verifyNoInteractions(inventarioClient);
        verify(pedidoRepositorio, never()).save(any(Pedido.class));
    }

    @Test
    void inventarioCaidoAlCancelarNoGuarda(){
        UUID clienteId = UUID.randomUUID();
        Pedido pedido = pedidoValido(clienteId);

        when(pedidoRepositorio.findById(pedido.getId())).thenReturn(Optional.of(pedido));
        doThrow(new ServicioNoDisponibleException("El servicio de inventarios se encontro caido al liberar"))
                .when(inventarioClient).liberar(any(UUID.class));

        assertThatThrownBy(() -> servicio.cambiarEstado(pedido.getId(), EstadoPedido.CANCELADO))
                .isInstanceOf(ServicioNoDisponibleException.class);

        verify(pedidoRepositorio, never()).save(any(Pedido.class));
    }

    @Test
    void confirmarPedidoCreadoNoLiberaReservas(){
        UUID clienteId = UUID.randomUUID();
        Pedido pedido = pedidoValido(clienteId);

        when(pedidoRepositorio.findById(pedido.getId())).thenReturn(Optional.of(pedido));

        PedidoResponse respuesta = servicio.cambiarEstado(pedido.getId(), EstadoPedido.CONFIRMADO);

        assertThat(respuesta.estado()).isEqualTo(EstadoPedido.CONFIRMADO);
        verifyNoInteractions(inventarioClient);
        verify(pedidoRepositorio).save(pedido);
    }
}
