package com.tiendaonline.pedidos.controlador;

import com.tiendaonline.pedidos.dominio.PedidoNoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ManejadorDeErrores extends ResponseEntityExceptionHandler {
    @ExceptionHandler(PedidoNoEncontradoException.class)
    public ProblemDetail pedidoNoEncontrado(PedidoNoEncontradoException ex){
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Pedido no encontrado");
        return problemDetail;
    }
}
