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

    public static ReservaDTO ReservaToDTO(Reserva reserva){
        return ReservaDTO.builder()
                .id(reserva.getId())
                .productoId(reserva.getProductoId())
                .pedidoId(reserva.getPedidoId())
                .cantidad(reserva.getCantidad())
                .estado(reserva.getEstado())
                .fechaCreacion(reserva.getFechaCreacion())
                .build();
    }
    public static Reserva DTOToReserva(ReservaDTO reserva){
        return Reserva.builder()
                .id(reserva.getId())
                .productoId(reserva.getProductoId())
                .pedidoId(reserva.getPedidoId())
                .cantidad(reserva.getCantidad())
                .estado(reserva.getEstado())
                .fechaCreacion(reserva.getFechaCreacion())
                .build();
    }
}
