package com.tiendaonline.pedidos.servicio;

import com.tiendaonline.pedidos.dominio.EstadoPedido;
import com.tiendaonline.pedidos.dominio.ItemPedido;
import com.tiendaonline.pedidos.dominio.Pedido;
import com.tiendaonline.pedidos.dominio.PedidoNoEncontradoException;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PedidoServiceTest {
    @Mock
    private PedidoRepository pedidoRepositorio;

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
}
