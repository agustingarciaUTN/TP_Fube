package com.tiendaonline.clientes.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Schema(
        name = "ItemPedidoResponseDTO",
        description = "Item dentro de un pedido, se retorna dentro de un pedido"
)
//@Schema es para documentar cada atributo en swagger
public record ItemPedidoResponseDTO(
        @Schema(
                description = "Identificador único asignado automáticamente al item",
                example = "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d"
        )
        UUID id,
        @Schema(
                description = "Identificador único asignado automáticamente al producto",
                example = "d9307190-9557-4372-b4a5-24a561e890ff"
        )
        UUID productoId,
        @Schema(
                description = "Cantidad del producto agregado en el item",
                example = "4"
        )
        int cantidad,
        @Schema(
                description = "Precio unitario del producto agregado en el item",
                example = "11,5"
        )
        BigDecimal precioUnitario
) {
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
