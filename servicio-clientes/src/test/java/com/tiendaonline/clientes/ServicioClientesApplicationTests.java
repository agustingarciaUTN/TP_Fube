package com.tiendaonline.clientes;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest(properties = "STATSIG_SECRET_KEY=llave-de-prueba")
class ServicioClientesApplicationTests {

    @Test
    void contextLoads() {
    }
}
