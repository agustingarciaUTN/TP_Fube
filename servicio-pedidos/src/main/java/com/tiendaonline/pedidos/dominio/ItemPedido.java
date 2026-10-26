package com.tiendaonline.pedidos.dominio;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;


@Getter
public class ItemPedido {
    private int cantidad;
    private BigDecimal precioUnitario;
    private UUID productoId;
    private UUID reservaId;


    public ItemPedido(int cantidad, BigDecimal precioUnitario, UUID productoId, UUID reservaId) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }

        if (precioUnitario == null) {
            throw new IllegalArgumentException("El precio unitario no puede ser Null");
        }
        if (precioUnitario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio unitario debe ser mayor a cero");
        }
        if (reservaId == null) {
            throw new IllegalArgumentException("La reserva no puede ser Null");
        }
        if (productoId == null) {
            throw new IllegalArgumentException("El producto no puede ser Null");
        }

        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.productoId = productoId;
        this.reservaId = reservaId;

    }

    public BigDecimal subtotal() {
        return this.precioUnitario.multiply(new BigDecimal(this.cantidad));
    }


}
