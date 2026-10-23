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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)//Por defecto se retorna 201, si hay excepcion se trata en el RestExceptionHandler
    public ReservaDTO reservarProducto(@RequestBody ReservaDTO reserva){
        return servReserva.reservarProducto(reserva);
    }
    @PatchMapping("/{reservaId}/liberacion")
    @ResponseStatus(HttpStatus.OK)//Por defecto se retorna 200 OK, si hay excepcion se trata en el RestExceptionHandler
    public ReservaDTO liberarReserva(@PathVariable UUID reservaId){
        return servReserva.liberarReserva(reservaId);
    }
}
