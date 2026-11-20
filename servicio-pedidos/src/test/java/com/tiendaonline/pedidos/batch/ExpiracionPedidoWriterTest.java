package com.tiendaonline.pedidos.batch;

import com.tiendaonline.pedidos.client.InventarioClient;
import com.tiendaonline.pedidos.client.ServicioNoDisponibleException;
import com.tiendaonline.pedidos.dominio.ItemPedido;
import com.tiendaonline.pedidos.dominio.Pedido;
import com.tiendaonline.pedidos.repositorio.PedidoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.Chunk;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ExpiracionPedidoWriterTest {
    @Mock
    private InventarioClient inventarioClient;

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private ExpiracionPedidoWriter expiracionPedidoWriter;

    @Test
    void pedidoCon2ItemsSeLiberaYGuarda(){
        UUID reservaId = UUID.randomUUID();
        UUID reservaId2 = UUID.randomUUID();
        ItemPedido itemPedido = new ItemPedido(2, new BigDecimal("250"), UUID.randomUUID(), reservaId);
        ItemPedido itemPedido2 = new ItemPedido(1, new BigDecimal("200"), UUID.randomUUID(), reservaId2);
        Pedido pedido = new Pedido(UUID.randomUUID(), List.of(itemPedido, itemPedido2));

        expiracionPedidoWriter.write(new Chunk<Pedido>(List.of(pedido)));

        verify(inventarioClient).liberar(reservaId);
        verify(inventarioClient).liberar(reservaId2);
        verify(pedidoRepository).save(pedido);
    }

    @Test
    void inventarioCaidoAlLiberarNoGuardaPedido(){
        UUID reservaId = UUID.randomUUID();
        ItemPedido itemPedido = new ItemPedido(2, new BigDecimal("250"), UUID.randomUUID(), reservaId);
        Pedido pedido = new Pedido(UUID.randomUUID(), List.of(itemPedido));

        doThrow(new ServicioNoDisponibleException("Inventario caido"))
                .when(inventarioClient).liberar(reservaId);

        assertThatThrownBy(() -> expiracionPedidoWriter.write(new Chunk<>(List.of(pedido))))
                .isInstanceOf(ServicioNoDisponibleException.class);

        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void expirarDosPedidosLiberaYGuardaCadaUno(){
        UUID reservaId = UUID.randomUUID();
        UUID  reservaId2 = UUID.randomUUID();
        ItemPedido itemPedido = new ItemPedido(2, new BigDecimal("250"), UUID.randomUUID(), reservaId);
        ItemPedido itemPedido2 = new ItemPedido(1, new BigDecimal("200"), UUID.randomUUID(), reservaId2);
        Pedido pedido = new Pedido(UUID.randomUUID(), List.of(itemPedido));
        Pedido pedido2 = new Pedido(UUID.randomUUID(), List.of(itemPedido2));


        expiracionPedidoWriter.write(new Chunk<Pedido>(List.of(pedido,  pedido2)));

        verify(inventarioClient).liberar(reservaId);
        verify(inventarioClient).liberar(reservaId2);
        verify(pedidoRepository).save(pedido);
        verify(pedidoRepository).save(pedido2);
    }
}
