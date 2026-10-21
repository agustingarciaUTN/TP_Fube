package com.tiendaonline.pedidos.dominio;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@AllArgsConstructor

public class ItemPedido {
    private int cantidad;
    private BigDecimal precioUnitario;

    public BigDecimal subtotal(){
        return this.precioUnitario.multiply(new BigDecimal(this.cantidad));
    }


}
