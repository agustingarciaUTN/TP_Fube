package com.tiendaonline.pedidos.dominio;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;


public class EstadoPedidoTest {
    //TDD
    //Test rojo

    @Test
    //Desde creado puedo pasar a confirmado -> espera true
     public void transicionCorrecta(){
        assertThat(EstadoPedido.CREADO.transicionValida(EstadoPedido.CONFIRMADO)).isTrue();
    }


}
