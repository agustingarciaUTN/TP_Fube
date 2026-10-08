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
    private int cantidadDisponible;

    public static ProductoDTO ProductoToDTO(Producto prod){
        return ProductoDTO.builder()
                .id(prod.getId())
                .nombre(prod.getNombre())
                .cantidadDisponible(prod.getCantidadDisponible())
                .build();
    }
}