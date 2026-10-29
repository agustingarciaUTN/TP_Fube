package com.tiendaonline.clientes.excepciones;

public class NombreExistenteException extends RuntimeException {
    public NombreExistenteException(String message) {
        super(message);
    }
}
