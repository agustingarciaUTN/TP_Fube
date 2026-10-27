package com.tiendaonline.inventario.DTOs;

import com.tiendaonline.inventario.modelo.Reserva;
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
public class ReservaDTO {
    private UUID id;
    private UUID productoId;
    private UUID pedidoId;
    private int cantidad;
    private String estado;
    private LocalDateTime fechaCreacion;
}
