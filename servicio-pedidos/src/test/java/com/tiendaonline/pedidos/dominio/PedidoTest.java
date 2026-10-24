package com.tiendaonline.pedidos.dominio;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PedidoTest {

    private final UUID idCliente = UUID.randomUUID();

    @Test
    public void pedidoNuevoNaceCreadoConCliente(){
        ItemPedido item = new ItemPedido(2, BigDecimal.valueOf(100));
        Pedido pedido = new Pedido(idCliente, List.of(item));

        assertThat(pedido.getEstado()).isEqualTo(EstadoPedido.CREADO);
        assertThat(pedido.getClienteId()).isEqualTo(idCliente);
    }

    @Test
    public void pedidoSinCliente(){
        ItemPedido item = new ItemPedido(2, BigDecimal.valueOf(100));
        assertThatThrownBy(() -> new Pedido(null, List.of(item)) )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Un pedido debe tener asignado un cliente");
    }

    @Test
    public void deberiaSumarSubtotales(){
        ItemPedido item1 = new ItemPedido(1, BigDecimal.valueOf(100));
        ItemPedido item2 = new ItemPedido(2, BigDecimal.valueOf(200));

        Pedido pedido = new Pedido(idCliente, List.of(item1, item2));
        BigDecimal sumaTotal = pedido.montoTotal();

        assertThat(sumaTotal).isEqualByComparingTo(BigDecimal.valueOf(500));
    }

    @Test
    public void deberiaLanzarExcepcionSinItems(){

        assertThatThrownBy(() -> new Pedido(idCliente, List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Un pedido debe tener al menos un item asignado.");

    }

    @Test
    public void deberiaLanzarExcepcionConListaNull(){

        assertThatThrownBy(() -> new Pedido(idCliente, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Un pedido debe tener al menos un item asignado.");

    }

    @Test
    public void cambiarEstadoValido(){
        ItemPedido item = new ItemPedido(2, BigDecimal.valueOf(100));
        ItemPedido item2 = new ItemPedido(2, BigDecimal.valueOf(200));

        Pedido pedido = new Pedido(idCliente, List.of(item, item2));

        pedido.cambiarEstado(EstadoPedido.CONFIRMADO);
        assertThat(pedido.getEstado()).isEqualTo(EstadoPedido.CONFIRMADO);
    }

    @Test
    public void cambiarEstadoInvalido(){
        ItemPedido item = new ItemPedido(2, BigDecimal.valueOf(100));
        ItemPedido item2 = new ItemPedido(2, BigDecimal.valueOf(200));

        Pedido pedido = new Pedido(idCliente, List.of(item, item2));
        pedido.cambiarEstado(EstadoPedido.CANCELADO);
        EstadoPedido estadoDestino = EstadoPedido.CONFIRMADO;

        assertThatThrownBy(() -> pedido.cambiarEstado(estadoDestino))
                .isInstanceOf(TransicionInvalidaException.class)
                .hasMessage("Un pedido no puede estar CANCELADO y luego ser CONFIRMADO");
        assertThat(pedido.getEstado()).isEqualTo(EstadoPedido.CANCELADO);
    }

    @Test
    public void pedidoNuevoConFechaCreacion(){
        ItemPedido item = new ItemPedido(2, BigDecimal.valueOf(100));

        LocalDateTime antesDeCrear = LocalDateTime.now();
        Pedido pedido = new Pedido(idCliente, List.of(item));

        assertThat(pedido.getFechaCreacion()).isNotNull();
        assertThat(pedido.getFechaCreacion()).isAfterOrEqualTo(antesDeCrear);
    }

    @Test
    public void cambiarEstadoActualizaFechaActualizacion(){
        ItemPedido item = new ItemPedido(2, BigDecimal.valueOf(100));
        Pedido pedido = new Pedido(idCliente, List.of(item));
        LocalDateTime fechaActualizacion = pedido.getFechaActualizacion();

        assertThat(pedido.getFechaActualizacion()).isNotNull();
        pedido.cambiarEstado(EstadoPedido.CONFIRMADO);

        assertThat(pedido.getFechaActualizacion()).isNotNull();
        assertThat(pedido.getFechaActualizacion()).isAfterOrEqualTo(fechaActualizacion);
    }
}
