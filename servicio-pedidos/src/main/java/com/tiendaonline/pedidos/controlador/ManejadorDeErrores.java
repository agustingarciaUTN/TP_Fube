package com.tiendaonline.pedidos.controlador;

import com.tiendaonline.pedidos.client.ServicioNoDisponibleException;
import com.tiendaonline.pedidos.dominio.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class ManejadorDeErrores extends ResponseEntityExceptionHandler {

    @ExceptionHandler(PedidoNoEncontradoException.class)
    public ProblemDetail pedidoNoEncontrado(PedidoNoEncontradoException ex){
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        log.warn("Pedido no encontrado: {}", ex.getMessage());
        problemDetail.setTitle("Pedido no encontrado");
        return problemDetail;
    }

    @ExceptionHandler(ClienteNoEncontradoException.class)
    public ProblemDetail clienteNoEncontrado(ClienteNoEncontradoException ex){
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        log.warn("Cliente no encontrado: {}", ex.getMessage());
        problemDetail.setTitle("Cliente no encontrado");
        return problemDetail;
    }

    @ExceptionHandler(ProductoNoEncontradoException.class)
    public ProblemDetail productoNoEncontrado(ProductoNoEncontradoException ex){
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        log.warn("Producto no encontrado: {}", ex.getMessage());
        problemDetail.setTitle("Producto no encontrado");
        return problemDetail;
    }

    @ExceptionHandler(StockInsuficienteException.class)
    public ProblemDetail stockInsuficiente(StockInsuficienteException ex){
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        log.warn("Stock insuficiente: {}", ex.getMessage());
        problemDetail.setTitle("Stock insuficiente");
        return problemDetail;
    }

    @ExceptionHandler(ServicioNoDisponibleException.class)
    public ProblemDetail servicioNoDisponible(ServicioNoDisponibleException ex){
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
        log.error("Servicio no disponible: {}", ex.getMessage());
        problemDetail.setTitle("El servicio no se encuentra disponible");
        return problemDetail;
    }

    @ExceptionHandler(TransicionInvalidaException.class)
    public ProblemDetail transicionInvalida(TransicionInvalidaException ex){
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        log.warn("Transicion invalida: {}", ex.getMessage());
        problemDetail.setTitle("Transicion de estado invalida");
        return problemDetail;
    }
}
