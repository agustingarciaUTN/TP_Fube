package com.tiendaonline.inventario.Servicios;

import com.tiendaonline.inventario.DTOs.ProductoDTO;
import com.tiendaonline.inventario.Excepciones.ProductoNoEncontradoException;
import com.tiendaonline.inventario.Excepciones.StockNoSuficienteException;
import com.tiendaonline.inventario.mappers.ProductoMapper;
import com.tiendaonline.inventario.modelo.Producto;
import com.tiendaonline.inventario.repositorios.RepositorioProductos;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ServicioInventarioImpl implements ServicioInventario{
    private final RepositorioProductos repositorioProductos;

    //Funcion para consultar stock, recibe un id de Producto y retorna un ProductoDTO
    @Override
    public int consultarStock(UUID id){
        //Busca en el repositorio por id, si encuentra un producto -> map() a DTO
        //Si no encuentra ningún producto con ese id -> orElseThrow() excepcion de producto no encontrado
        ProductoDTO producto = repositorioProductos.findById(id)
                .map(ProductoMapper::toDTO)
                .orElseThrow( () -> new ProductoNoEncontradoException(
                        "El id introducido no corresponde a ningún producto")
        );
        //Retorna en stock disponible del producto
        return producto.getCantidadDisponible();
    }

    //Funcion para reducir el stock de un producto, recibe id de producto y cantidad a reducir
    @Override
    public ProductoDTO reducirStock(UUID id, int cantidad){
        //Busca en el repositorio por id
        //Si no encuentra ningún producto con ese id -> orElseThrow() excepcion de producto no encontrado
        Producto producto = repositorioProductos.findById(id)
                .orElseThrow( () -> new ProductoNoEncontradoException(
                        "El id introducido no corresponde a ningún producto")
        );
        //Se crea la variable stockActual con el valor de la cantidad de producto disponible
        int stockActual = producto.getCantidadDisponible();
        //Si hay suficiente stock para reducir cantidad sin que se haga negativo
        if(stockActual>=cantidad){
            //Se reduce el stock en cantidad
            producto.setCantidadDisponible(stockActual-cantidad);
            //Se guarda el producto con el nuevo stock reducido y se retorna
            return ProductoMapper.toDTO(repositorioProductos.save(producto));
        }else{
            //Si no hay suficiente stock se lanza excepcion
            throw new StockNoSuficienteException("No hay stock suficiente de "
                    + producto.getNombre() + " stock actual = " + stockActual
                    + " cantidad solicitada = " + cantidad);
        }
    }

    //Funcion para aumentar stock de un producto, recibe id de Producto y cantidad a aumentar
    @Override
    public ProductoDTO aumentarStock(UUID id, int cantidad){
        //Busca en el repositorio por id
        //Si no encuentra ningún producto con ese id -> orElseThrow() excepcion de producto no encontrado
        Producto producto = repositorioProductos.findById(id)
                .orElseThrow( () -> new ProductoNoEncontradoException(
                        "El id introducido no corresponde a ningún producto")
        );
        //Se crea la variable stockActual con el valor de la cantidad de producto disponible
        int stockActual = producto.getCantidadDisponible();
        //Se setea el nuevo valor de stock, aumentando el mismo en cantidad
        producto.setCantidadDisponible(stockActual+cantidad);
        //Se guarda el producto con el nuevo stock aumentado y se retorna
        return ProductoMapper.toDTO(repositorioProductos.save(producto));
    }
}
