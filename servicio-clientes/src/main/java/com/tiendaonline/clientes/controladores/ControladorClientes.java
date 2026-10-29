package com.tiendaonline.clientes.controladores;

import com.tiendaonline.clientes.DTOs.ClienteDTO;
import com.tiendaonline.clientes.servicios.ServicioClientesImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("clientes")
@AllArgsConstructor
public class ControladorClientes {
    private final ServicioClientesImpl servicioClientes;

    @ResponseStatus(HttpStatus.CREATED) //Por defecto se retorna 201 CREATED, si hay excepcion se trata en el RestExceptionHandler
    @PostMapping
    public ClienteDTO registrarCliente(@RequestBody ClienteDTO cliente){
        return servicioClientes.registrarCliente(cliente);
    }
}
