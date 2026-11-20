package com.tiendaonline.pedidos.dto;

import com.tiendaonline.pedidos.dominio.EstadoPedido;
import com.tiendaonline.pedidos.dominio.Pedido;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "Pedido con sus items y su estado")
public record PedidoResponse(
        @Schema(
                description = "Id del pedido"
        )UUID id,
        @Schema(
                description = "Id del cliente"
        )UUID clienteId,
        @Schema(
                description = "CREADO, CONFIRMADO, CANCELADO o EXPIRADO"
        )EstadoPedido estado,
        @Schema(
                description = "Suma de cantidad por precio unitario de cada item"
        ) BigDecimal montoTotal,
        @Schema(
                description = "Fecha de alta del pedido"
        )LocalDateTime fechaCreacion,
        @Schema(
                description = "Fecha del ultimo cambio de estado"
        )LocalDateTime fechaActualizacion,
        @Schema(
                description = "Items del pedido, con el precio tomado de inventario al crearlo"
        )List<ItemPedidoResponse> items) {
    public static PedidoResponse desde(Pedido pedido) {
        List<ItemPedidoResponse> itemsResponse = pedido.getItems().stream().map(ItemPedidoResponse::desde).toList();

        return new PedidoResponse(pedido.getId(), pedido.getClienteId(), pedido.getEstado(),
                pedido.getMontoTotal(), pedido.getFechaCreacion(), pedido.getFechaActualizacion(), itemsResponse
        );
    }
}
