package com.tiendaonline.pedidos.dominio;

public enum EstadoPedido {
    CREADO,
    CONFIRMADO,
    CANCELADO,
    EXPIRADO;



    public boolean transicionValida(EstadoPedido destino){
        return true;
    }

}

