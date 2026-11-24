package com.tiendaonline.pedidos.client;

public class ServicioNoDisponibleException extends RuntimeException {
    public ServicioNoDisponibleException(String message) {
        super(message);
    }

    public ServicioNoDisponibleException(String message, Throwable cause) {
        super(message, cause);
    }
}
