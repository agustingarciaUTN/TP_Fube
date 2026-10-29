package com.tiendaonline.clientes.excepciones;

public class NombreInvalidoException extends RuntimeException {
    public NombreInvalidoException(String message) {
        super(message);
    }
}
