package com.tiendaonline.inventario.Servicios;

import com.tiendaonline.inventario.DTOs.ProductoDTO;
import com.tiendaonline.inventario.Excepciones.ProductoNoEncontradoException;
import com.tiendaonline.inventario.modelo.Producto;
import com.tiendaonline.inventario.repositorios.RepositorioProductos;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ServicioInventarioImpl implements ServicioInventario{
    private final RepositorioProductos repositorioProductos;

    @Override
    public ProductoDTO consultarStock(UUID id){
        Producto prod = repositorioProductos.findById(id).orElseThrow(
                () -> new ProductoNoEncontradoException("El id introducido no corresponde a ningún producto")
        );
        return ProductoDTO.ProductoToDTO(prod);
    }
}
