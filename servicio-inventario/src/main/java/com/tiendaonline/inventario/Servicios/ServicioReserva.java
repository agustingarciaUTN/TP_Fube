package com.tiendaonline.inventario.Servicios;

import com.tiendaonline.inventario.DTOs.ReservaDTO;

import java.util.UUID;

public interface ServicioReserva {
    public ReservaDTO reservarProducto(ReservaDTO res);
    public ReservaDTO liberarReserva(UUID reservaId);
}
