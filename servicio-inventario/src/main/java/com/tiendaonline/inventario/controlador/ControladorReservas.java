package com.tiendaonline.inventario.controlador;

import com.tiendaonline.inventario.DTOs.ErrorDTO;
import com.tiendaonline.inventario.DTOs.ReservaDTO;
import com.tiendaonline.inventario.Servicios.ServicioReservaImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Reservas", description = "Operaciones relacionadas con las reservas de la tienda")
//Para swagger. Se accede en: http://localhost:8083/swagger-ui/index.html
//O en OpenAPI: http://localhost:8083/v3/api-docs
@RestController
@RequestMapping("/v1/reservas")
@AllArgsConstructor
public class ControladorReservas {
    public final ServicioReservaImpl servReserva;

    @Operation(summary = "Reservar un producto", description = "Crea y guarda una reserva de un producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reserva creada y producto reservado"),
            @ApiResponse(responseCode = "404", description = "No existe un producto con el id introducido",
                    content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "409", description = "No hay stock suficiente para reservar el producto ",
                    content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    })//Establece las respuestas que se muestran en swagger
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)//Por defecto se retorna 201, si hay excepcion se trata en el RestExceptionHandler
    public ReservaDTO reservarProducto(@RequestBody ReservaDTO reserva){
        return servReserva.reservarProducto(reserva);
    }

    @Operation(summary = "Liberar una reserva", description = "Marca reserva como liberada y libera el stock reservado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reserva y producto liberados"),
            @ApiResponse(responseCode = "404", description = "No existe una reserva con el id introducido",
                    content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "409", description = "La reserva seleccionada ya estaba liberada",
                    content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    })//Establece las respuestas que se muestran en swagger
    @PatchMapping("/{reservaId}/liberacion")
    @ResponseStatus(HttpStatus.OK)//Por defecto se retorna 200 OK, si hay excepcion se trata en el RestExceptionHandler
    public ReservaDTO liberarReserva(@PathVariable UUID reservaId){
        return servReserva.liberarReserva(reservaId);
    }
}
