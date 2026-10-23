package com.tiendaonline.pedidos.dominio;

import lombok.Getter;

import java.math.BigDecimal;


@Getter


public class ItemPedido {
    private int cantidad;
    private BigDecimal precioUnitario;


    public ItemPedido(int cantidad, BigDecimal precioUnitario) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }

        if (precioUnitario == null) {
            throw new IllegalArgumentException("El precio unitario no puede ser Null");}
        if (precioUnitario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio unitario debe ser mayor a cero");}


        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;

    }

    public BigDecimal subtotal() {
        return this.precioUnitario.multiply(new BigDecimal(this.cantidad));
    }


}
