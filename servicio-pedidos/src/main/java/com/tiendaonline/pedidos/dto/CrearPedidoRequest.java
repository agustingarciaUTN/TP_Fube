package com.tiendaonline.pedidos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

@Schema(description = "Datos para crear un pedido")
public record CrearPedidoRequest(

        @Schema(
                description = "Id del cliente que hace el pedido",
                example = "ad10043d-56ca-43fb-8799-e7ca95eb7018"
        )
        @NotNull UUID clienteId,

        @Schema(
                description = "Productos del pedido, al menos uno"
        )
        @NotEmpty @Valid List<ItemPedidoRequest> items) {
}
