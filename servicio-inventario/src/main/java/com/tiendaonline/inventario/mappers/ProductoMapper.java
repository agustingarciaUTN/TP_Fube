package com.tiendaonline.inventario.mappers;

import com.tiendaonline.inventario.DTOs.ProductoDTO;
import com.tiendaonline.inventario.modelo.Producto;

public class ProductoMapper {
    public static ProductoDTO toDTO(Producto producto){
        return ProductoDTO.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .sku(producto.getSku())
                .cantidadDisponible(producto.getCantidadDisponible())
                .precioUnitario(producto.getPrecioUnitario())
                .build();
    }

}
