package com.tiendaonline.inventario.mappers;

import com.tiendaonline.inventario.DTOs.ReservaDTO;
import com.tiendaonline.inventario.modelo.Reserva;

public class ReservaMapper {
    public static ReservaDTO toDTO(Reserva reserva){
        return ReservaDTO.builder()
                .id(reserva.getId())
                .productoId(reserva.getProductoId())
                .pedidoId(reserva.getPedidoId())
                .cantidad(reserva.getCantidad())
                .estado(reserva.getEstado())
                .fechaCreacion(reserva.getFechaCreacion())
                .build();
    }

    public static Reserva toEntity(ReservaDTO reserva){
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
