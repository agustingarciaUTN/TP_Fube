package com.tiendaonline.pedidos.dominio;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ItemPedidoTest {

    @Test
    public void subtotalEsCantidadPorPrecioUnitario(){
        ItemPedido item = new ItemPedido(2, new BigDecimal("10.50"));

        BigDecimal resultado = item.subtotal();

        assertThat(resultado).isEqualByComparingTo(new BigDecimal("21.00"));
    }


    @ParameterizedTest
    @CsvSource({
            " 0,  10.50, 'La cantidad debe ser mayor a cero'",  // Cantidad cero
            "-1,  10.50, 'La cantidad debe ser mayor a cero'",  // Cantidad negativa
            " 2,   0.00, 'El precio unitario debe ser mayor a cero'",    // Precio unitario cero
            " 2,  -1.50, 'El precio unitario debe ser mayor a cero'"     // Precio unitario negativo
    })
    public void deberiaLanzarExcepcionParaDatosInvalidos(int cantidad,  String precioUnitarioString, String mensajeEsperado){

        BigDecimal precioUnitario = new BigDecimal(precioUnitarioString);

        assertThatThrownBy(()-> new ItemPedido(cantidad, precioUnitario))
        .isInstanceOf(IllegalArgumentException.class).hasMessage(mensajeEsperado);

    }

    @Test
    public void deberiaLanzarExcepcionParaValorNull(){
        BigDecimal precioUnitario = null;
        assertThatThrownBy(() -> new ItemPedido(2, precioUnitario))
        .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El precio unitario no puede ser Null");
    }


}
