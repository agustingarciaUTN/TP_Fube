package com.tiendaonline.inventario.Excepciones;

public class StockNoSuficienteException extends RuntimeException {
    public StockNoSuficienteException(String message) {
        super(message);
    }
}
