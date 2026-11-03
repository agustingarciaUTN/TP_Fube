package com.tiendaonline.clientes.controladores;

import com.tiendaonline.clientes.DTOs.ErrorDTO;
import com.tiendaonline.clientes.excepciones.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ManejadorDeExcecionesRest {
    @ExceptionHandler(EmailExistenteException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorDTO manejarEmailExistenteException(EmailExistenteException ex){
        return new ErrorDTO(409,"CONFLICT", ex.getMessage());
    }
    @ExceptionHandler(NombreExistenteException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorDTO manejarNombreExistenteException(NombreExistenteException ex){
        return new ErrorDTO(409,"CONFLICT", ex.getMessage());
    }
    @ExceptionHandler(NombreInvalidoException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO manejarNombreInvalidoException(NombreInvalidoException ex){
        return new ErrorDTO(400,"BAD_REQUEST", ex.getMessage());
    }
    @ExceptionHandler(EmailInvalidoException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO manejarEmailInvalidoException(EmailInvalidoException ex){
        return new ErrorDTO(400,"BAD_REQUEST", ex.getMessage());
    }
    @ExceptionHandler(ClienteNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDTO manejarClienteNoEncontradoException(ClienteNoEncontradoException ex){
        return new ErrorDTO(404,"NOT_FOUND", ex.getMessage());
    }
    @ExceptionHandler(ServicioExternoNoDisponibleException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorDTO manejarServicioExternoNoDisponibleException(ServicioExternoNoDisponibleException ex){
        return new ErrorDTO(503,"SERVICE_UNAVAILABLE", ex.getMessage());
    }
}
