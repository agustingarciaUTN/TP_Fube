package com.tiendaonline.inventario.controlador;

import com.tiendaonline.inventario.DTOs.ErrorDTO;
import com.tiendaonline.inventario.Excepciones.ProductoNoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler(ProductoNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDTO handleProductoNoEncontrado(ProductoNoEncontradoException ex){
        return new ErrorDTO("NOT_FOUND", ex.getMessage());
    }
}
