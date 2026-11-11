package com.tiendaonline.clientes.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Schema(
        name = "PedidoResponseDTO",
        description = "Pedido de un cliente, se retorna dentro de una lista de pedidos"
)
//@Schema es para documentar cada atributo en swagger
public record PedidoResponseDTO(
        @Schema(
                description = "Identificador único asignado automáticamente al pedido",
                example = "35dc8345-858b-4153-98d4-7cf74eb99a5a"
        )
        UUID id,
        @Schema(
                description = "Identificador único asignado automáticamente al cliente",
                example = "d9307190-9557-4372-b4a5-24a561e890ff"
        )
        UUID clienteId,
        @Schema(
                description = "Estado del pedido",
                example = "ACTIVO"
        )
        String estado,
        @Schema(
                description = "Monto total del pedido",
                example = "52.5"
        )
        BigDecimal montoTotal,
        @Schema(
                description = "Fecha de creación del pedido",
                example = "2026-11-06T19:23:02"
        )
        LocalDateTime fechaCreacion,
        @Schema(
                description = "Fecha de última actualización del pedido",
                example = "2026-12-07T08:19:15"
        )
        LocalDateTime fechaActualizacion,
        @Schema(
                description = "Lista de items que contiene el pedido"
        )
        List<ItemPedidoResponseDTO> items
) {
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
