package com.tiendaonline.inventario.controlador;

import com.tiendaonline.inventario.DTOs.ReservaDTO;
import com.tiendaonline.inventario.Servicios.ServicioReservaImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/reservas")
@AllArgsConstructor
public class ControladorReservas {
    public final ServicioReservaImpl servReserva;
//No funciona
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ReservaDTO reservarProducto(@RequestBody ReservaDTO reserva){
        return servReserva.reservarProducto(reserva);
    }
}
