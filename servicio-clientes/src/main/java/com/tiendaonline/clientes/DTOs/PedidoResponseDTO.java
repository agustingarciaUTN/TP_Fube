package com.tiendaonline.clientes.DTOs;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record PedidoResponseDTO(UUID id, UUID clienteId, String estado, BigDecimal montoTotal,
                                LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion,
                                List<ItemPedidoResponseDTO> items) {
    public UUID getId(){
        return id;
    }
    public UUID getClienteId(){
        return clienteId;
    }
    public String getEstado(){
        return estado;
    }
    public BigDecimal getMontoTotal(){
        return montoTotal;
    }
    public LocalDateTime getFechaCreacion(){
        return fechaCreacion;
    }
    public LocalDateTime getFechaActualizacion(){
        return fechaActualizacion;
    }
    public List<ItemPedidoResponseDTO> getItems(){
        return items;
    }

}
