package com.tiendaonline.pedidos.repositorio;


import com.tiendaonline.pedidos.dominio.EstadoPedido;
import com.tiendaonline.pedidos.dominio.ItemPedido;
import com.tiendaonline.pedidos.dominio.Pedido;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class PedidoRepositoryTest {

    @Autowired
    private PedidoRepository repositorioPedido;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void guardarYRecuperar() {
        UUID clienteId = UUID.randomUUID();
        UUID reservaId = UUID.randomUUID();
        UUID reservaId2 = UUID.randomUUID();

        ItemPedido itemPedido = new ItemPedido(2, new BigDecimal("10.50"), UUID.randomUUID(), reservaId);
        ItemPedido itemPedido2 = new ItemPedido(1, new BigDecimal("11.50"), UUID.randomUUID(), reservaId2);

        Pedido pedido = new Pedido(clienteId, List.of(itemPedido, itemPedido2));

        repositorioPedido.save(pedido);
        entityManager.flush();
        entityManager.clear();

        Pedido recuperado = repositorioPedido.findById(pedido.getId()).orElseThrow();

        assertThat(recuperado.getClienteId()).isEqualTo(clienteId);
        assertThat(recuperado.getEstado()).isEqualTo(EstadoPedido.CREADO);
        assertThat(recuperado.getMontoTotal()).isEqualByComparingTo("32.50");
        assertThat(recuperado.getItems()).hasSize(2);
        assertThat(recuperado.getItems()).extracting(ItemPedido::getReservaId).containsExactlyInAnyOrder(reservaId2, reservaId);
    }

    @Test
    public void buscarPorClienteDevuelveSoloSusPedidos() {
        UUID clienteIdA = UUID.randomUUID();
        UUID clienteIdB = UUID.randomUUID();

        ItemPedido itemPedido = new ItemPedido(2, new BigDecimal("10.50"), UUID.randomUUID(), UUID.randomUUID());
        ItemPedido itemPedido2 = new ItemPedido(1, new BigDecimal("11.50"), UUID.randomUUID(), UUID.randomUUID());
        ItemPedido itemPedido3 = new ItemPedido(1, new BigDecimal("12.50"), UUID.randomUUID(), UUID.randomUUID());

        Pedido pedidoA1 = new Pedido(clienteIdA, List.of(itemPedido));
        Pedido pedidoA2 = new Pedido(clienteIdA, List.of(itemPedido2));
        Pedido pedidoB1 = new Pedido(clienteIdB, List.of(itemPedido3));

        repositorioPedido.save(pedidoA1);
        repositorioPedido.save(pedidoA2);
        repositorioPedido.save(pedidoB1);

        entityManager.flush();
        entityManager.clear();

        List<Pedido> pedidosClienteA = repositorioPedido.findByClienteId(clienteIdA);
        assertThat(pedidosClienteA).hasSize(2);
        assertThat(pedidosClienteA).extracting(Pedido::getClienteId).containsOnly(clienteIdA);

    }
}