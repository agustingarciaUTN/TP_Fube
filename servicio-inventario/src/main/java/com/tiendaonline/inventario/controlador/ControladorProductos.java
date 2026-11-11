package com.tiendaonline.inventario.controlador;

import com.tiendaonline.inventario.DTOs.ErrorDTO;
import com.tiendaonline.inventario.Servicios.ServicioInventarioImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Productos", description = "Operaciones relacionadas con los productos de la tienda")
//Para swagger. Se accede en: http://localhost:8083/swagger-ui/index.html
//O en OpenAPI: http://localhost:8083/v3/api-docs
@RestController
@RequestMapping("/v1/productos")
@AllArgsConstructor
public class ControladorProductos {
    private final ServicioInventarioImpl servicioInventario;

    @Operation(summary = "Consultar stock de un producto", description = "Retorna la cantidad disponible de un producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta de stock correcta"),
            @ApiResponse(responseCode = "404", description = "No existe un producto con el id introducido",
                    content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    })//Establece las respuestas que se muestran en swagger
    @GetMapping(path = "/{id}/stock", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK) //Por defecto se retorna 200 OK, si hay excepcion se trata en el RestExceptionHandler
    public int consultarStockProducto(@PathVariable UUID id){
        return servicioInventario.consultarStock(id);
    }

    @Operation(summary = "Consultar el precio de un producto", description = "Retorna el precio de un producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta de precio correcta"),
            @ApiResponse(responseCode = "404", description = "No existe un producto con el id introducido",
                    content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    })//Establece las respuestas que se muestran en swagger
    @GetMapping("/{id}/precio")
    @ResponseStatus(HttpStatus.OK) //Por defecto se retorna 200 OK, si hay excepcion se trata en el RestExceptionHandler
    public Float consultarPrecioProducto(@PathVariable UUID id){
        return servicioInventario.consultarPrecio(id);
    }
}
