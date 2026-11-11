package com.tiendaonline.inventario.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
        name = "ReservaDTO",
        description = "Objeto que contiene los datos de la reserva"
)
public class ReservaDTO {
    //@Schema es para documentar cada atributo en swagger
    @Schema(description = "Identificador único asignado automáticamente a la reserva", example = "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d", accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;
    @Schema(description = "Identificador único del producto", example = "d9307190-9557-4372-b4a5-24a561e890ff")
    private UUID productoId;
    @Schema(description = "Identificador único del pedido", example = "35dc8345-858b-4153-98d4-7cf74eb99a5a")
    private UUID pedidoId;
    @Schema(description = "Cantidad reservada del producto", example = "5")
    private int cantidad;
    @Schema(description = "Estado de la reserva", example = "ACTIVA")
    private String estado;
    @Schema(description = "Fecha de creación de la reserva", example = "2026-11-03T18:30:01")
    private LocalDateTime fechaCreacion;
}
