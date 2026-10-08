package com.tiendaonline.inventario.Servicios;

import com.tiendaonline.inventario.DTOs.ProductoDTO;

import java.util.UUID;

public interface ServicioInventario {
    public ProductoDTO consultarStock(UUID id);
}
