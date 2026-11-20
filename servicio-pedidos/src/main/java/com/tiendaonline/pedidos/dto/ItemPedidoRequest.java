package com.tiendaonline.pedidos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

@Schema(
        description = "Producto y cantidad a pedir"
)
public record ItemPedidoRequest(

        @Schema(
                description = "Id del producto en inventario",
                example = "7075ac78-fd3b-4510-b693-0e16c3e26dac"
        )
        @NotNull UUID productoId,

        @Schema(
                description = "Unidades a pedir, mayor a cero",
                example = "2"
        )
        @NotNull @Positive Integer cantidad) {
}
