package com.tiendaonline.pedidos.dto;

import com.tiendaonline.pedidos.dominio.EstadoPedido;
import com.tiendaonline.pedidos.dominio.Pedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PedidoResponse(UUID id, UUID clienteId, EstadoPedido estado, BigDecimal montoTotal,
                             LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion,
                             List<ItemPedidoResponse> items) {
    public static PedidoResponse desde(Pedido pedido) {
        List<ItemPedidoResponse> itemsResponse = pedido.getItems().stream().map(ItemPedidoResponse::desde).toList();

        return new PedidoResponse(pedido.getId(), pedido.getClienteId(), pedido.getEstado(),
                pedido.getMontoTotal(), pedido.getFechaCreacion(), pedido.getFechaActualizacion(), itemsResponse
        );
    }
}
