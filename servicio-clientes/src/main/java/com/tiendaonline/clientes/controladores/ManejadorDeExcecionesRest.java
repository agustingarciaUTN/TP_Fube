package com.tiendaonline.clientes.controladores;

import com.tiendaonline.clientes.DTOs.ErrorDTO;
import com.tiendaonline.clientes.excepciones.EmailExistenteException;
import com.tiendaonline.clientes.excepciones.EmailInvalidoException;
import com.tiendaonline.clientes.excepciones.NombreExistenteException;
import com.tiendaonline.clientes.excepciones.NombreInvalidoException;
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
}
