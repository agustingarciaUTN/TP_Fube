package com.tiendaonline.clientes.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(
        name = "ListaPedidosResponse",
        description = "Respuesta con el historial de pedidos de un cliente"
)
//@Schema es para documentar cada atributo en swagger
public record ListaPedidosResponseDTO(
        @Schema(
                description = "Lista de pedidos asociados al cliente"
        )
        List<PedidoResponseDTO> pedidos,
        @Schema(
                description = "Cantidad total de pedidos registrados para el cliente",
                example = "3",
                minimum = "0"
        )
        long total
) {
    public List<PedidoResponseDTO> getPedidos(){
        return pedidos;
    }
}
