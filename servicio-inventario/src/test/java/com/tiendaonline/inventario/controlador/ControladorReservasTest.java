package com.tiendaonline.inventario.controlador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiendaonline.inventario.DTOs.ReservaDTO;
import com.tiendaonline.inventario.Excepciones.ProductoNoEncontradoException;
import com.tiendaonline.inventario.Excepciones.ReservaLiberadaException;
import com.tiendaonline.inventario.Excepciones.ReservaNoExistenteException;
import com.tiendaonline.inventario.Excepciones.StockNoSuficienteException;
import com.tiendaonline.inventario.Servicios.ServicioReservaImpl;
import com.tiendaonline.inventario.modelo.Reserva;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ControladorReservas.class)
class ControladorReservasTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ServicioReservaImpl servicioReserva;

    private ReservaDTO ejReservaDTOEntrada(){
        return ReservaDTO.builder()
                .productoId(UUID.fromString("a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d"))
                .pedidoId(UUID.fromString("35dc8345-858b-4153-98d4-7cf74eb99a5a"))
                .cantidad(5)
                .build();
    }
    private ReservaDTO ejReservaDTOSalida(){
        return ReservaDTO.builder()
                .productoId(UUID.fromString("a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d"))
                .pedidoId(UUID.fromString("35dc8345-858b-4153-98d4-7cf74eb99a5a"))
                .estado("ACTIVA")
                .cantidad(5)
                .fechaCreacion(LocalDateTime.of(2026,8,10,12,4, 1))
                .build();
    }

    @Test
    void reservarProducto_Controlador_Correcto() throws Exception{
        ReservaDTO reservaEntrada = ejReservaDTOEntrada();
        ReservaDTO reservaSalida = ejReservaDTOSalida();
        given(servicioReserva.reservarProducto(reservaEntrada)).willReturn(reservaSalida);

        mockMvc.perform(post("/v1/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservaEntrada)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productoId").value(ejReservaDTOSalida().getProductoId().toString()))
                .andExpect(jsonPath("$.pedidoId").value(ejReservaDTOSalida().getPedidoId().toString()))
                .andExpect(jsonPath("$.estado").value("ACTIVA"))
                .andExpect(jsonPath("$.fechaCreacion").value(ejReservaDTOSalida().getFechaCreacion().toString()));

        verify(servicioReserva, times(1)).reservarProducto(reservaEntrada);
    }

    @Test
    void reservarProducto_Controlador_Fallo_ProductoNoEncontrado() throws Exception{
        ReservaDTO reservaEntrada = ejReservaDTOEntrada();
        ReservaDTO reservaSalida = ejReservaDTOSalida();
        given(servicioReserva.reservarProducto(reservaEntrada)).willThrow(
                new ProductoNoEncontradoException("El id introducido no corresponde a ningún producto")
        );

        mockMvc.perform(post("/v1/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservaEntrada)))
                .andExpect(status().isNotFound());

        verify(servicioReserva, times(1)).reservarProducto(reservaEntrada);
    }

    @Test
    void reservarProducto_Controlador_Fallo_StockInsuficiente() throws Exception{
        ReservaDTO reservaEntrada = ejReservaDTOEntrada();
        ReservaDTO reservaSalida = ejReservaDTOSalida();
        given(servicioReserva.reservarProducto(reservaEntrada)).willThrow(
                new StockNoSuficienteException("No hay stock suficiente del producto")
        );

        mockMvc.perform(post("/v1/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservaEntrada)))
                .andExpect(status().isConflict());

        verify(servicioReserva, times(1)).reservarProducto(reservaEntrada);
    }


    @Test
    void liberarReserva_Controlador_Correcto() throws Exception{
        ReservaDTO reservaEntrada = ejReservaDTOEntrada();
        reservaEntrada.setId(UUID.fromString("d9307190-9557-4372-b4a5-24a561e890ff"));
        ReservaDTO reservaSalida = ejReservaDTOSalida();
        reservaSalida.setEstado("LIBERADA");

        given(servicioReserva.liberarReserva(reservaEntrada.getId())).willReturn(reservaSalida);

        mockMvc.perform(patch("/v1/reservas/" + reservaEntrada.getId() + "/liberacion")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productoId").value(reservaSalida.getProductoId().toString()))
                .andExpect(jsonPath("$.pedidoId").value(reservaSalida.getPedidoId().toString()))
                .andExpect(jsonPath("$.estado").value(reservaSalida.getEstado()))
                .andExpect(jsonPath("$.fechaCreacion").value(reservaSalida.getFechaCreacion().toString()));

        verify(servicioReserva, times(1)).liberarReserva(reservaEntrada.getId());
    }

    @Test
    void liberarReserva_Controlador_Fallo_ReservaLiberada() throws Exception{
        ReservaDTO reservaEntrada = ejReservaDTOEntrada();
        reservaEntrada.setId(UUID.fromString("d9307190-9557-4372-b4a5-24a561e890ff"));
        reservaEntrada.setEstado("LIBERADA");

        given(servicioReserva.liberarReserva(reservaEntrada.getId())).willThrow(
                new ReservaLiberadaException("No se puedo liberar la reserva con el id = "
                        + reservaEntrada.getId() + " porque ya se encontraba liberada")
        );

        mockMvc.perform(patch("/v1/reservas/" + reservaEntrada.getId() + "/liberacion")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());

        verify(servicioReserva, times(1)).liberarReserva(reservaEntrada.getId());
    }

    @Test
    void liberarReserva_Controlador_Fallo_ReservaInexistente() throws Exception{
        ReservaDTO reservaEntrada = ejReservaDTOEntrada();
        reservaEntrada.setId(UUID.fromString("d9307190-9557-4372-b4a5-24a561e890ff"));

        given(servicioReserva.liberarReserva(reservaEntrada.getId())).willThrow(
                new ReservaNoExistenteException(
                        "No existe una reserva con el id = " + reservaEntrada.getId())
        );

        mockMvc.perform(patch("/v1/reservas/" + reservaEntrada.getId() + "/liberacion")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(servicioReserva, times(1)).liberarReserva(reservaEntrada.getId());
    }
}