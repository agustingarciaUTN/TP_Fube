package com.tiendaonline.pedidos.dominio;

import org.junit.jupiter.api.Test;

public class ItemPedidoTest {

    @Test
    public void ItemPedidoTest(){
        ItemPedido item = new ItemPedido(2, new BigDecimal("10.50"));

        BigDecimal resultado = item.subtotal();

        assertThat(resultado).isEqualByComparingTo(new BigDecimal("21.00"));
    }


}
