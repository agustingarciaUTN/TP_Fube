package com.tiendaonline.inventario.servicios;

import com.tiendaonline.inventario.DTOs.ProductoDTO;
import com.tiendaonline.inventario.Excepciones.ProductoNoEncontradoException;
import com.tiendaonline.inventario.Excepciones.StockNoSuficienteException;
import com.tiendaonline.inventario.Servicios.ServicioInventarioImpl;
import com.tiendaonline.inventario.modelo.Producto;
import com.tiendaonline.inventario.repositorios.RepositorioProductos;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ServicioInventarioImplTest {
    @Mock
    private RepositorioProductos repositorioProductos;

    @InjectMocks
    private ServicioInventarioImpl servicioInventario;

    public ProductoDTO ejProducto1DTO(){
        return ProductoDTO.builder()
                .id(UUID.fromString("a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d"))
                .nombre("Monitor 24 Pulgadas IPS")
                .sku("PROD-TEST-001")
                .cantidadDisponible(15)
                .build();
    }

    public Producto ejProducto1Entity(){
        return Producto.builder()
                .id(UUID.fromString("a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d"))
                .nombre("Monitor 24 Pulgadas IPS")
                .sku("PROD-TEST-001")
                .cantidadDisponible(15)
                .build();
    }

    @Test
    void consultarStock_Correcto() {
        ProductoDTO productoDTO = ejProducto1DTO();
        UUID idDTO = productoDTO.getId();
        Producto producto = ejProducto1Entity();
        given(repositorioProductos.findById(idDTO)).willReturn(Optional.of(producto));

        int stock = servicioInventario.consultarStock(idDTO);

        assertThat(stock).isEqualTo(15);
        verify(repositorioProductos, times(1)).findById(idDTO);
    }

    @Test
    void consultarStock_Fallo_ProductoNoExiste() {
        UUID idDTO = ejProducto1DTO().getId();
        given(repositorioProductos.findById(idDTO)).willReturn(Optional.empty());

        ProductoNoEncontradoException excepcion = assertThrows(
                ProductoNoEncontradoException.class,
                () -> servicioInventario.consultarStock(idDTO)
        );

        assertEquals("El id introducido no corresponde a ningún producto", excepcion.getMessage());
        verify(repositorioProductos, times(1)).findById(idDTO);
    }

    @Test
    void reducirStock_Correcto() {
        ProductoDTO productoDTO = ejProducto1DTO();
        UUID idDTO = productoDTO.getId();
        Producto producto = ejProducto1Entity();
        int cantidad = 5;

        given(repositorioProductos.findById(idDTO)).willReturn(Optional.of(producto));
        given(repositorioProductos.save(any(Producto.class))).willAnswer(invocation -> invocation.getArgument(0));

        ProductoDTO resultado = servicioInventario.reducirStock(idDTO, cantidad);

        assertNotNull(resultado);
        assertEquals(10, resultado.getCantidadDisponible());

        verify(repositorioProductos, times(1)).findById(idDTO);

        ArgumentCaptor<Producto> captor = ArgumentCaptor.forClass(Producto.class);
        verify(repositorioProductos, times(1)).save(captor.capture());
        Producto productoGuardado = captor.getValue();
        assertEquals(10, productoGuardado.getCantidadDisponible());
    }

    @Test
    void reducirStock_Fallo_ProductoNoEncontrado() {
        ProductoDTO productoDTO = ejProducto1DTO();
        UUID idDTO = productoDTO.getId();
        int cantidad = 5;

        given(repositorioProductos.findById(idDTO)).willReturn(Optional.empty());

        ProductoNoEncontradoException excepcion = assertThrows(
                ProductoNoEncontradoException.class,
                () -> servicioInventario.reducirStock(idDTO, cantidad)
        );

        verify(repositorioProductos, times(1)).findById(idDTO);
        verify(repositorioProductos, never()).save(any(Producto.class));
        assertEquals("El id introducido no corresponde a ningún producto", excepcion.getMessage());
    }

    @Test
    void reducirStock_Fallo_StockInsuficiente() {
        ProductoDTO productoDTO = ejProducto1DTO();
        UUID idDTO = productoDTO.getId();
        Producto producto = ejProducto1Entity();
        int cantidad = 20;

        given(repositorioProductos.findById(idDTO)).willReturn(Optional.of(producto));

        StockNoSuficienteException excepcion = assertThrows(
                StockNoSuficienteException.class,
                () -> servicioInventario.reducirStock(idDTO, cantidad)
        );

        verify(repositorioProductos, times(1)).findById(idDTO);
        verify(repositorioProductos, never()).save(any(Producto.class));
        assertEquals("No hay stock suficiente de "
                + producto.getNombre() + " stock actual = " + producto.getCantidadDisponible()
                + " cantidad solicitada = " + cantidad, excepcion.getMessage());
    }

    @Test
    void aumentarStock_Correcto() {
        ProductoDTO productoDTO = ejProducto1DTO();
        UUID idDTO = productoDTO.getId();
        Producto producto = ejProducto1Entity();
        int cantidad = 5;

        given(repositorioProductos.findById(idDTO)).willReturn(Optional.of(producto));
        given(repositorioProductos.save(any(Producto.class))).willAnswer(invocation -> invocation.getArgument(0));

        ProductoDTO resultado = servicioInventario.aumentarStock(idDTO, cantidad);

        assertNotNull(resultado);
        assertEquals(20, resultado.getCantidadDisponible());

        verify(repositorioProductos, times(1)).findById(idDTO);

        ArgumentCaptor<Producto> captor = ArgumentCaptor.forClass(Producto.class);
        verify(repositorioProductos, times(1)).save(captor.capture());
        Producto productoGuardado = captor.getValue();
        assertEquals(20, productoGuardado.getCantidadDisponible());
    }

    @Test
    void aumentarStock_Fallo_ProductoNoEncontrado() {
        ProductoDTO productoDTO = ejProducto1DTO();
        UUID idDTO = productoDTO.getId();
        int cantidad = 5;

        given(repositorioProductos.findById(idDTO)).willReturn(Optional.empty());

        ProductoNoEncontradoException excepcion = assertThrows(
                ProductoNoEncontradoException.class,
                () -> servicioInventario.aumentarStock(idDTO, cantidad)
        );

        verify(repositorioProductos, times(1)).findById(idDTO);
        verify(repositorioProductos, never()).save(any(Producto.class));
        assertEquals("El id introducido no corresponde a ningún producto", excepcion.getMessage());
    }

}