package com.tiendaonline.inventario.controlador;

import com.tiendaonline.inventario.DTOs.ProductoDTO;
import com.tiendaonline.inventario.Servicios.ServicioInventarioImpl;
import com.tiendaonline.inventario.modelo.Producto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/productos")
@AllArgsConstructor
public class ControladorInventario {
    private final ServicioInventarioImpl servicioInventario;

    @GetMapping("/{id}/stock")
    @ResponseStatus(HttpStatus.OK)
    public ProductoDTO consultarStockProducto(@PathVariable UUID id){
        return servicioInventario.consultarStock(id);
    }
}
