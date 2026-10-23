package com.tiendaonline.inventario.DTOs;


import com.tiendaonline.inventario.modelo.Producto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoDTO {
    private UUID id;
    private String nombre;
    private String sku;
    private int cantidadDisponible;

}