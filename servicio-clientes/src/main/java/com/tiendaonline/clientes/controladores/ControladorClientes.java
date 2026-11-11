package com.tiendaonline.clientes.controladores;

import com.tiendaonline.clientes.DTOs.ClienteDTO;
import com.tiendaonline.clientes.DTOs.ErrorDTO;
import com.tiendaonline.clientes.DTOs.ListaPedidosResponseDTO;
import com.tiendaonline.clientes.servicios.ServicioClientesImpl;
import com.tiendaonline.clientes.statsig.GestorFeatureFlag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import com.statsig.*;

import java.util.UUID;


@Tag(name = "Clientes", description = "Operaciones relacionadas con los clientes de la tienda")
//Para swagger. Se accede en: http://localhost:8081/swagger-ui/index.html
//O en OpenAPI: http://localhost:8081/v3/api-docs
@RestController
@RequestMapping("/v1/clientes")
@AllArgsConstructor
public class ControladorClientes {
    private final ServicioClientesImpl servicioClientes;

    @Operation(summary = "Registrar un cliente nuevo", description = "Crea un cliente nuevo validando email y nombre únicos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente registrado con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "409", description = "Nombre o email ya registrados",
                    content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    })//Establece las respuestas que se muestran en swagger
    @ResponseStatus(HttpStatus.CREATED) //Por defecto se retorna 201 CREATED, si hay excepcion se trata en el RestExceptionHandler
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ClienteDTO registrarCliente(@RequestBody ClienteDTO cliente){
        return servicioClientes.registrarCliente(cliente);
    }


    @Operation(summary = "Consultar datos de un cliente", description = "Retorna los datos de un cliente enviando el id del mismo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta de clientes correcta"),
            @ApiResponse(responseCode = "404", description = "No existe cliente con el id introducido",
                    content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    })//Establece las respuestas que se muestran en swagger
    @ResponseStatus(HttpStatus.OK) //Por defecto se retorna 200 OK, si hay excepcion se trata en el RestExceptionHandler
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ClienteDTO consultarCliente(@PathVariable UUID id)
    {
        return servicioClientes.consultarClientePorId(id);
    }

    @Operation(summary = "Listar los pedidos que realizó un cliente", description = "Retorna un listado con los pedidos que realizo el cliente, se envía el id del. mismo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta de listado de clientes correcta"),
            @ApiResponse(responseCode = "404", description = "No existe cliente con el id introducido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "503", description = "El servicio para consultar los pedidos no está disponible o tiene un error",
                    content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    })//Establece las respuestas que se muestran en swagger
    @ResponseStatus(HttpStatus.OK) //Por defecto se retorna 200 OK, si hay excepcion se trata en el RestExceptionHandler
    @GetMapping(path = "/{id}/pedidos", produces = MediaType.APPLICATION_JSON_VALUE)
    public ListaPedidosResponseDTO listarPedidosCliente(@PathVariable UUID id){
        return servicioClientes.listarPedidosCliente(id);
    }
}
