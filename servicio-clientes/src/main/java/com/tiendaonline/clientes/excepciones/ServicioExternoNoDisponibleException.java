package com.tiendaonline.clientes.excepciones;

public class ServicioExternoNoDisponibleException extends RuntimeException {
    public ServicioExternoNoDisponibleException(String message) {
        super(message);
    }
}
