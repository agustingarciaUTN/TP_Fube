package com.tiendaonline.pedidos.dto;

import com.tiendaonline.pedidos.dominio.EstadoPedido;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(
        description = "Datos para cambiar el estado de un pedido"
)
public record CambiarEstadoRequest(
        @Schema(
                description = "Estado destino: CONFIRMADO o CANCELADO",
                example = "CANCELADO"
        )
        @NotNull EstadoPedido estado) {
}
