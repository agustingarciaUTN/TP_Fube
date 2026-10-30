package com.tiendaonline.pedidos.dominio;

public class PedidoNoEncontradoException extends RuntimeException {
    public PedidoNoEncontradoException(String message) {
        super(message);
    }
}
