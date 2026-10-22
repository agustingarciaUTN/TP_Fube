package com.tiendaonline.inventario.Servicios;

import com.tiendaonline.inventario.DTOs.ProductoDTO;
import com.tiendaonline.inventario.Excepciones.ProductoNoEncontradoException;
import com.tiendaonline.inventario.Excepciones.StockNoSuficienteException;
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
    @Override
    public ProductoDTO reducirStock(UUID id, int cantidad){
        Producto prod = repositorioProductos.findById(id).orElseThrow(
                () -> new ProductoNoEncontradoException("El id introducido no corresponde a ningún producto")
        );
        int stockActual = prod.getCantidadDisponible();
        if(stockActual>=cantidad){
            prod.setCantidadDisponible(stockActual-cantidad);
            repositorioProductos.save(prod); //Se guarda el producto con el nuevo stock
        }else{
            throw new StockNoSuficienteException("No hay stock suficiente de "
                    + prod.getNombre() + " stock actual = " + stockActual
                    + " cantidad solicitada = " + cantidad);
        }
        return ProductoDTO.ProductoToDTO(prod);
    }

    @Override
    public ProductoDTO aumentarStock(UUID id, int cantidad){
        Producto prod = repositorioProductos.findById(id).orElseThrow(
                () -> new ProductoNoEncontradoException("El id introducido no corresponde a ningún producto")
        );
        int stockActual = prod.getCantidadDisponible();
        prod.setCantidadDisponible(stockActual+cantidad);
        repositorioProductos.save(prod); //Se guarda el producto con el nuevo stock
        return ProductoDTO.ProductoToDTO(prod);
    }
}
