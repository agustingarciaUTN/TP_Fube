package com.tiendaonline.inventario.Servicios;

import com.tiendaonline.inventario.DTOs.ProductoDTO;
import com.tiendaonline.inventario.DTOs.ReservaDTO;
import com.tiendaonline.inventario.Excepciones.StockNoSuficienteException;
import com.tiendaonline.inventario.repositorios.RepositorioReserva;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ServicioReservaImpl implements ServicioReserva{
    private final RepositorioReserva repositorioReserva;
    private final ServicioInventarioImpl servicioInventario;

    public ReservaDTO reservarProducto(ReservaDTO res){
        int cantidad = res.getCantidad();
        UUID idProducto = res.getProductoId();
        UUID idPedido = res.getPedidoId();
        ProductoDTO producto = servicioInventario.consultarStock(idProducto);
        ReservaDTO reserva = ReservaDTO.builder()
                .productoId(idProducto)
                .pedidoId(idPedido)
                .estado("ACTIVA")
                .cantidad(cantidad)
                .fechaCreacion(LocalDateTime.now())
                .build();
        if (producto.getCantidadDisponible() >= cantidad){
            return guardarReserva(reserva);
        }else{
            throw new StockNoSuficienteException("No hay stock suficiente para reservar "
                    + producto.getNombre() + "Stock actual = " + producto.getCantidadDisponible()
                    + " cantidad solicitada = " + cantidad);
        }
    }

    //Uso transactional para evitar errores si se produce un error durante la ejecucion del metodo
    @Transactional
    public ReservaDTO guardarReserva(ReservaDTO reserva){
        //Reducir cantidad en Producto
        servicioInventario.reducirStock(reserva.getProductoId(), reserva.getCantidad());
        //Guardar la reserva y retornar ReservaDTO
        return ReservaDTO.ReservaToDTO(repositorioReserva.save(ReservaDTO.DTOToReserva(reserva)));
    }
}
