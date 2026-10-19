package com.tiendaonline.pedidos.dominio;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;


public class EstadoPedidoTest {
    //TDD
    //Test rojo

    @Test
    //Desde creado puedo pasar a confirmado -> espera true
     public void transicionCorrecta(){
        assertThat(EstadoPedido.CREADO.transicionValida(EstadoPedido.CONFIRMADO)).isTrue();
    }

    @Test
    //Desde cancelado no debe pasar a confirmado
     public void canceladoNoHaciaConfirmado(){
        assertThat(EstadoPedido.CANCELADO.transicionValida(EstadoPedido.CONFIRMADO)).isFalse();
    }


    @ParameterizedTest( name = "Transicion de {0} a {1} debe ser {2}")
    @CsvSource({
            // ESTADO_DESDE, ESTADO_HACIA, VALIDEZ_ESPERADA
            "CREADO,     CONFIRMADO, true",
            "CREADO,     CANCELADO,  true",
            "CREADO,     EXPIRADO,    true",
            "CREADO,     CREADO,    false",
            "CONFIRMADO, EXPIRADO,    false",
            "CONFIRMADO, CANCELADO,  true",
            "CONFIRMADO, CREADO,     false",
            "CONFIRMADO, CONFIRMADO,     false",
            "EXPIRADO,    CANCELADO,  false",
            "EXPIRADO,    CREADO,     false",
            "EXPIRADO,   CONFIRMADO,  false",
            "EXPIRADO,   EXPIRADO,  false",
            "CANCELADO,  CONFIRMADO, false",
            "CANCELADO,  CREADO, false",
            "CANCELADO,  EXPIRADO, false",
            "CANCELADO,  CANCELADO, false"
    })
    void validarTransicionesDeEstado(EstadoPedido desde, EstadoPedido hacia, boolean validezEsperada){
        boolean resultado = desde.transicionValida(hacia);

        assertThat(resultado).isEqualTo(validezEsperada);
    }

}
