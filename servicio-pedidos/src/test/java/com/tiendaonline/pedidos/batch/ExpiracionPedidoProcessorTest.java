package com.tiendaonline.pedidos.batch;

import com.tiendaonline.pedidos.dominio.EstadoPedido;
import com.tiendaonline.pedidos.dominio.ItemPedido;
import com.tiendaonline.pedidos.dominio.Pedido;
import com.tiendaonline.pedidos.dominio.TransicionInvalidaException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ExpiracionPedidoProcessorTest {
    private final ExpiracionPedidoProcessor expiracionPedidoProcessor = new ExpiracionPedidoProcessor();

    @Test
    void creadoSaleComoExpirado() {
        UUID clienteId = UUID.randomUUID();
        ItemPedido item = new ItemPedido(2, new BigDecimal("100"), UUID.randomUUID(), UUID.randomUUID());
        Pedido pedido = new Pedido(clienteId, List.of(item));

        Pedido resultado = expiracionPedidoProcessor.process(pedido);

        Assertions.assertNotNull(resultado);
        assertThat(resultado.getEstado()).isEqualTo(EstadoPedido.EXPIRADO);
    }

    @Test
    void confirmadoLanzaExcepcion() {
        UUID clienteId = UUID.randomUUID();
        ItemPedido item = new ItemPedido(2, new BigDecimal("100"), UUID.randomUUID(), UUID.randomUUID());
        Pedido pedido = new Pedido(clienteId, List.of(item));

        pedido.cambiarEstado(EstadoPedido.CONFIRMADO);

        assertThatThrownBy(() -> expiracionPedidoProcessor.process(pedido))
                .isInstanceOf(TransicionInvalidaException.class);
    }
}
