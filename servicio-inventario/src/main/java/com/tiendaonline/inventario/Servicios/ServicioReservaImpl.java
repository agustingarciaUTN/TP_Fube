package com.tiendaonline.inventario.Servicios;

import com.tiendaonline.inventario.DTOs.ReservaDTO;
import com.tiendaonline.inventario.Excepciones.ReservaLiberadaException;
import com.tiendaonline.inventario.Excepciones.ReservaNoExistenteException;
import com.tiendaonline.inventario.mappers.ReservaMapper;
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

    //Funcion para reservar producto, recibe un ReservaDTO, necesita cantidad, id de Producto e id de Pedido
    @Transactional //Es transactional para que si falla luego de reducir stock, esta acción se revierta
    @Override
    public ReservaDTO reservarProducto(ReservaDTO res){
        //Se crean las variables cantidad, idProducto e idPedido con los valores de la reserva recibida
        int cantidad = res.getCantidad();
        UUID idProducto = res.getProductoId();
        UUID idPedido = res.getPedidoId();
        //Se genera la reserva con los datos necesarios
        ReservaDTO reserva = ReservaDTO.builder()
                .productoId(idProducto)
                .pedidoId(idPedido)
                .estado("ACTIVA")
                .cantidad(cantidad)
                .fechaCreacion(LocalDateTime.now())
                .build();

        //Reducir cantidad en Producto
        servicioInventario.reducirStock(reserva.getProductoId(), reserva.getCantidad());
        //Guardar la reserva y retornar ReservaDTO
        return ReservaMapper.toDTO(repositorioReserva.save(ReservaMapper.toEntity(reserva)));
    }

    //Funcion para liberar la reserva, recibe el id de la reserva
    @Transactional
    @Override
    public ReservaDTO liberarReserva(UUID reservaId){
        //Busca la reserva por id, si la encuentra la convierte a DTO, si no la encuentra lanza excepcion
        ReservaDTO reserva = repositorioReserva.findById(reservaId)
                .map(ReservaMapper::toDTO)
                .orElseThrow( () -> new ReservaNoExistenteException(
                        "No existe una reserva con el id = " + reservaId));
        //Si el estado de la reserva es "ACTIVA"
        if(reserva.getEstado().equals("ACTIVA") ){
            //Se modifica por "LIBERADA" y se guarda (funcion Transactional)
            reserva.setEstado("LIBERADA");
            //Aumentar cantidad en Producto
            servicioInventario.aumentarStock(reserva.getProductoId(), reserva.getCantidad());
            //Guardar la reserva y retornar ReservaDTO
            return ReservaMapper.toDTO(repositorioReserva.save(ReservaMapper.toEntity(reserva)));

        }else{ //Si la reserva ya estaba en estado "LIBERADA" se lanza una excepcion para notificar
            throw new ReservaLiberadaException("No se puedo liberar la reserva con el id = "
                    + reservaId + " porque ya se encontraba liberada");
        }

    }
}
