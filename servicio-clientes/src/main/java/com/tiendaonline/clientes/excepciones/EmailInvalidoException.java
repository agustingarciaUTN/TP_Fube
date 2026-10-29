package com.tiendaonline.clientes.excepciones;

public class EmailInvalidoException extends RuntimeException {
    public EmailInvalidoException(String message) {
        super(message);
    }
}
