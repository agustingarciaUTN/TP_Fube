package com.tiendaonline.inventario.Servicios;

import com.tiendaonline.inventario.DTOs.ReservaDTO;

import java.util.UUID;

public interface ServicioReserva {
    public ReservaDTO reservarProducto(ReservaDTO res);
    public ReservaDTO guardarReserva(ReservaDTO reserva);
    public ReservaDTO liberarReserva(UUID reservaId);
    public ReservaDTO guardarReservaLiberada(ReservaDTO reserva);
}
