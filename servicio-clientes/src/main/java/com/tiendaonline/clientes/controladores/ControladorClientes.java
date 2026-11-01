package com.tiendaonline.clientes.controladores;

import com.tiendaonline.clientes.DTOs.ClienteDTO;
import com.tiendaonline.clientes.servicios.ServicioClientesImpl;
import com.tiendaonline.clientes.statsig.GestorFeatureFlag;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.statsig.*;

@RestController
@RequestMapping("v1/clientes")
@AllArgsConstructor
public class ControladorClientes {
    private final ServicioClientesImpl servicioClientes;
    private final GestorFeatureFlag gestorFeatureFlag;

    @ResponseStatus(HttpStatus.CREATED) //Por defecto se retorna 201 CREATED, si hay excepcion se trata en el RestExceptionHandler
    @PostMapping
    public ClienteDTO registrarCliente(@RequestBody ClienteDTO cliente){
        return servicioClientes.registrarCliente(cliente);
    }
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public boolean prueba(){
        return gestorFeatureFlag.isFlagActivo("mask-pii");
    }
}
