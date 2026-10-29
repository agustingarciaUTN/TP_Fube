package com.tiendaonline.pedidos.dto;

import com.tiendaonline.pedidos.dominio.ItemPedido;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemPedidoResponse(UUID id, UUID productoId, int cantidad, BigDecimal precioUnitario) {
    public static ItemPedidoResponse desde(ItemPedido itemDelPedido){
        return new ItemPedidoResponse(itemDelPedido.getId(), itemDelPedido.getProductoId(),
                itemDelPedido.getCantidad(), itemDelPedido.getPrecioUnitario());
    }
}
