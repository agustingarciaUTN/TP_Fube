package com.tiendaonline.pedidos.batch;

import com.tiendaonline.pedidos.dominio.EstadoPedido;
import com.tiendaonline.pedidos.dominio.Pedido;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class ExpiracionPedidoProcessor implements ItemProcessor<Pedido, Pedido> {

    @Override
    public Pedido process(Pedido pedido) {
        pedido.cambiarEstado(EstadoPedido.EXPIRADO);
        return pedido;
    }
}
