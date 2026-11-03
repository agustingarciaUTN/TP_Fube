package com.tiendaonline.clientes.DTOs;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record ItemPedidoResponseDTO(UUID id, UUID productoId, int cantidad, BigDecimal precioUnitario) {
    public UUID getId(){
        return id;
    }
    public UUID getProductoId(){
        return productoId;
    }
    public int getCantidad(){
        return cantidad;
    }
    public BigDecimal getPrecioUnitario(){
        return precioUnitario;
    }

}
