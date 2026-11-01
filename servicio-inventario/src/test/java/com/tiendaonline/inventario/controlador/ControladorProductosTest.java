package com.tiendaonline.inventario.controlador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiendaonline.inventario.Excepciones.ProductoNoEncontradoException;
import com.tiendaonline.inventario.Servicios.ServicioInventarioImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ControladorProductos.class)
class ControladorProductosTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ServicioInventarioImpl servicioInventario;

    @Test
    void consultarStock_Controlador_Correcto() throws Exception {
        UUID idProducto = UUID.fromString("a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d");
        int stock = 15;
        given(servicioInventario.consultarStock(idProducto)).willReturn(stock);

        mockMvc.perform(get("/v1/productos/" + idProducto + "/stock")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(stock)));

        verify(servicioInventario).consultarStock(idProducto);
    }

    @Test
    void consultarStock_Controlador_Fallo_ProductoNoEncontrado() throws Exception {
        UUID idProducto = UUID.fromString("a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d");
        int stock = 15;
        given(servicioInventario.consultarStock(idProducto))
                .willThrow( new ProductoNoEncontradoException(
                "El id introducido no corresponde a ningún producto"));

        mockMvc.perform(get("/v1/productos/" + idProducto + "/stock")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(servicioInventario).consultarStock(idProducto);
    }

    @Test
    void consultarPrecio_Controlador_Correcto() throws Exception {
        UUID idProducto = UUID.fromString("a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d");
        Float precioUnitario = 5.0F;
        given(servicioInventario.consultarPrecio(idProducto)).willReturn(precioUnitario);

        mockMvc.perform(get("/v1/productos/" + idProducto + "/precio")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(precioUnitario)));

        verify(servicioInventario).consultarPrecio(idProducto);
    }
    @Test
    void consultarPrecio_Controlador_Fallo_ProductoNoEncontrado() throws Exception {
        UUID idProducto = UUID.fromString("a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d");
        Float precioUnitario = 5.0F;
        given(servicioInventario.consultarPrecio(idProducto))
                .willThrow( new ProductoNoEncontradoException(
                "El id introducido no corresponde a ningún producto"));

        mockMvc.perform(get("/v1/productos/" + idProducto + "/precio")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(servicioInventario).consultarPrecio(idProducto);
    }

}