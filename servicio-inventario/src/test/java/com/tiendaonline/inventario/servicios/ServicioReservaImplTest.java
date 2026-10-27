package com.tiendaonline.inventario.servicios;

import com.tiendaonline.inventario.DTOs.ProductoDTO;
import com.tiendaonline.inventario.DTOs.ReservaDTO;
import com.tiendaonline.inventario.Excepciones.ProductoNoEncontradoException;
import com.tiendaonline.inventario.Excepciones.ReservaLiberadaException;
import com.tiendaonline.inventario.Excepciones.ReservaNoExistenteException;
import com.tiendaonline.inventario.Excepciones.StockNoSuficienteException;
import com.tiendaonline.inventario.Servicios.ServicioInventarioImpl;
import com.tiendaonline.inventario.Servicios.ServicioReserva;
import com.tiendaonline.inventario.Servicios.ServicioReservaImpl;
import com.tiendaonline.inventario.modelo.Producto;
import com.tiendaonline.inventario.modelo.Reserva;
import com.tiendaonline.inventario.repositorios.RepositorioReserva;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicioReservaImplTest {
    @Mock
    RepositorioReserva repositorioReserva;
    @Mock
    ServicioInventarioImpl servicioInventario;
    @InjectMocks
    ServicioReservaImpl servicioReserva;

    public ReservaDTO ejemploReservaDTO(){
        return ReservaDTO.builder()
                .id(UUID.fromString("b5e80a26-3349-424c-99b3-0c49c52864cf"))
                .productoId(UUID.fromString("a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d"))
                .pedidoId(UUID.fromString("c2b1c4d4-e5d2-818b-0a0f-8f2a3a2c7c5a"))
                .cantidad(5)
                .build();
    }

    public Reserva ejemploReservaEntity(){
        return Reserva.builder()
                .id(UUID.fromString("b5e80a26-3349-424c-99b3-0c49c52864cf"))
                .productoId(UUID.fromString("a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d"))
                .pedidoId(UUID.fromString("c2b1c4d4-e5d2-818b-0a0f-8f2a3a2c7c5a"))
                .cantidad(5)
                .build();
    }

    @Test
    void reservarProducto_Correcto() {
        ReservaDTO reservaDTO = ejemploReservaDTO();
        given(servicioInventario.reducirStock(reservaDTO.getProductoId(), reservaDTO.getCantidad()))
                .willReturn(ProductoDTO.builder().id(reservaDTO.getProductoId()).build());
        given(repositorioReserva.save(any(Reserva.class))).willAnswer(invocation -> invocation.getArgument(0));

        ReservaDTO resultado = servicioReserva.reservarProducto(reservaDTO);

        assertNotNull(resultado);
        assertEquals(reservaDTO.getProductoId(), resultado.getProductoId());
        assertEquals(reservaDTO.getPedidoId(), resultado.getPedidoId());
        assertEquals(reservaDTO.getCantidad(), resultado.getCantidad());
        assertEquals("ACTIVA", resultado.getEstado());

        ArgumentCaptor<Reserva> captor = ArgumentCaptor.forClass(Reserva.class);
        verify(repositorioReserva, times(1)).save(captor.capture());

        Reserva reservaGuardada = captor.getValue();
        assertEquals(reservaDTO.getProductoId(), reservaGuardada.getProductoId());
        assertEquals(reservaDTO.getPedidoId(), reservaGuardada.getPedidoId());
        assertEquals(reservaDTO.getCantidad(), reservaGuardada.getCantidad());
        assertEquals("ACTIVA", reservaGuardada.getEstado());
        assertNotNull(reservaGuardada.getFechaCreacion());
    }

    @Test
    void reservarProducto_Fallo_StockNoSuficiente() {
        ReservaDTO reservaDTO = ejemploReservaDTO();
        given(servicioInventario.reducirStock(reservaDTO.getProductoId(), reservaDTO.getCantidad()))
                .willThrow(new StockNoSuficienteException("No hay stock suficiente de "
                        + "ProductoEjemplo" + " stock actual = " + 1
                        + " cantidad solicitada = " + reservaDTO.getCantidad()));

        assertThrows(
                StockNoSuficienteException.class,
                () -> servicioReserva.reservarProducto(reservaDTO)
        );

        verify(repositorioReserva, never()).save(any(Reserva.class));
    }

    @Test
    void reservarProducto_Fallo_ProductoNoEncontrado() {
        ReservaDTO reservaDTO = ejemploReservaDTO();
        given(servicioInventario.reducirStock(reservaDTO.getProductoId(), reservaDTO.getCantidad()))
                .willThrow(new ProductoNoEncontradoException(
                        "El id introducido no corresponde a ningún producto"));

        assertThrows(
                ProductoNoEncontradoException.class,
                () -> servicioReserva.reservarProducto(reservaDTO)
        );

        verify(repositorioReserva, never()).save(any(Reserva.class));
    }


    @Test
    void liberarReserva_Correcto() {
        Reserva reservaEntity = ejemploReservaEntity();
        reservaEntity.setEstado("ACTIVA");
        reservaEntity.setFechaCreacion(LocalDateTime.now());

        given(repositorioReserva.findById(reservaEntity.getId())).willReturn(Optional.of(reservaEntity));
        given(servicioInventario.aumentarStock(reservaEntity.getProductoId(), reservaEntity.getCantidad()))
                .willReturn(ProductoDTO.builder().id(reservaEntity.getProductoId()).build());
        given(repositorioReserva.save(any(Reserva.class))).willAnswer(invocation -> invocation.getArgument(0));

        ReservaDTO resultado = servicioReserva.liberarReserva(reservaEntity.getId());

        assertNotNull(resultado);
        assertEquals(reservaEntity.getId(), resultado.getId());
        assertEquals(reservaEntity.getProductoId(), resultado.getProductoId());
        assertEquals(reservaEntity.getPedidoId(), resultado.getPedidoId());
        assertEquals(reservaEntity.getCantidad(), resultado.getCantidad());
        assertEquals("LIBERADA", resultado.getEstado());
        assertEquals(reservaEntity.getFechaCreacion(), resultado.getFechaCreacion());

        verify(repositorioReserva, times(1)).findById(reservaEntity.getId());
        verify(servicioInventario, times(1)).aumentarStock(reservaEntity.getProductoId(), reservaEntity.getCantidad());

        ArgumentCaptor<Reserva> captor = ArgumentCaptor.forClass(Reserva.class);
        verify(repositorioReserva, times(1)).save(captor.capture());

        Reserva reservaGuardada = captor.getValue();
        assertNotNull(resultado);
        assertEquals(reservaEntity.getId(), reservaGuardada.getId());
        assertEquals(reservaEntity.getProductoId(), reservaGuardada.getProductoId());
        assertEquals(reservaEntity.getPedidoId(), reservaGuardada.getPedidoId());
        assertEquals(reservaEntity.getCantidad(), reservaGuardada.getCantidad());
        assertEquals("LIBERADA", reservaGuardada.getEstado());
        assertEquals(reservaEntity.getFechaCreacion(), reservaGuardada.getFechaCreacion());
    }

    @Test
    void liberarReserva_Fallo_ReservaNoEncontrada() {
        Reserva reservaEntity = ejemploReservaEntity();

        given(repositorioReserva.findById(reservaEntity.getId())).willReturn(Optional.empty());

        assertThrows(
                ReservaNoExistenteException.class,
                () -> servicioReserva.liberarReserva(reservaEntity.getId())
        );

        verify(repositorioReserva, times(1)).findById(reservaEntity.getId());
        verify(servicioInventario, never()).aumentarStock(any(UUID.class), any(int.class));
        verify(repositorioReserva, never()).save(any(Reserva.class));
    }

    @Test
    void liberarReserva_Fallo_ReservaLiberada() {
        Reserva reservaEntity = ejemploReservaEntity();
        reservaEntity.setEstado("LIBERADA");
        reservaEntity.setFechaCreacion(LocalDateTime.now());

        given(repositorioReserva.findById(reservaEntity.getId())).willReturn(Optional.of(reservaEntity));

        assertThrows(
                ReservaLiberadaException.class,
                () -> servicioReserva.liberarReserva(reservaEntity.getId())
        );

        verify(repositorioReserva, times(1)).findById(reservaEntity.getId());
        verify(servicioInventario, never()).aumentarStock(any(UUID.class), any(int.class));
        verify(repositorioReserva, never()).save(any(Reserva.class));
    }

    @Test
    void liberarReserva_Fallo_ProductoNoEncontrado() {
        Reserva reservaEntity = ejemploReservaEntity();
        reservaEntity.setEstado("ACTIVA");
        reservaEntity.setFechaCreacion(LocalDateTime.now());

        given(repositorioReserva.findById(reservaEntity.getId())).willReturn(Optional.of(reservaEntity));
        given(servicioInventario.aumentarStock(reservaEntity.getProductoId(), reservaEntity.getCantidad()))
                .willThrow(new ProductoNoEncontradoException(
                        "El id introducido no corresponde a ningún producto"));

        assertThrows(
                ProductoNoEncontradoException.class,
                () -> servicioReserva.liberarReserva(reservaEntity.getId())
        );

        verify(repositorioReserva, times(1)).findById(reservaEntity.getId());
        verify(servicioInventario, times(1)).aumentarStock(any(UUID.class), any(int.class));
        verify(repositorioReserva, never()).save(any(Reserva.class));
    }

}