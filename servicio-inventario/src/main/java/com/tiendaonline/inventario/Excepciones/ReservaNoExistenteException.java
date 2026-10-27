package com.tiendaonline.inventario.Excepciones;

public class ReservaNoExistenteException extends RuntimeException {
    public ReservaNoExistenteException(String message) {
        super(message);
    }
}
