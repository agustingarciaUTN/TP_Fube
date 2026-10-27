package com.tiendaonline.inventario.controlador;

import com.tiendaonline.inventario.DTOs.ErrorDTO;
import com.tiendaonline.inventario.Excepciones.ProductoNoEncontradoException;
import com.tiendaonline.inventario.Excepciones.ReservaLiberadaException;
import com.tiendaonline.inventario.Excepciones.ReservaNoExistenteException;
import com.tiendaonline.inventario.Excepciones.StockNoSuficienteException;
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
    @ExceptionHandler(StockNoSuficienteException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorDTO handleStockNoSuficiente(StockNoSuficienteException ex){
        return new ErrorDTO("CONFLICT", ex.getMessage());
    }

    @ExceptionHandler(ReservaLiberadaException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorDTO handleStockNoSuficiente(ReservaLiberadaException ex){
        return new ErrorDTO("CONFLICT", ex.getMessage());
    }

    @ExceptionHandler(ReservaNoExistenteException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDTO handleProductoNoEncontrado(ReservaNoExistenteException ex){
        return new ErrorDTO("NOT_FOUND", ex.getMessage());
    }
}
