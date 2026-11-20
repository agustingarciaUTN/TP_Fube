package com.tiendaonline.pedidos.batch;


import com.tiendaonline.pedidos.client.InventarioClient;
import com.tiendaonline.pedidos.client.ServicioNoDisponibleException;
import com.tiendaonline.pedidos.dominio.EstadoPedido;
import com.tiendaonline.pedidos.dominio.ItemPedido;
import com.tiendaonline.pedidos.dominio.Pedido;
import com.tiendaonline.pedidos.dominio.ResumenExpiracion;
import com.tiendaonline.pedidos.repositorio.PedidoRepository;
import com.tiendaonline.pedidos.repositorio.ResumenExpiracionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@SpringBatchTest
public class ExpiracionPedidosJobTest {
    @MockBean
    InventarioClient inventarioClient;

    @Autowired
    JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    PedidoRepository pedidoRepository;

    @Autowired
    ResumenExpiracionRepository resumenExpiracionRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    void limpiar() {
        pedidoRepository.deleteAll();
        resumenExpiracionRepository.deleteAll();
    }

    private Pedido crearPedido(UUID reservaId) {
        ItemPedido item = new ItemPedido(2, new BigDecimal("100"), UUID.randomUUID(), reservaId);
        Pedido pedido = new Pedido(UUID.randomUUID(), List.of(item));
        return pedidoRepository.save(pedido);
    }

    private void envejecer(UUID pedidoId) {
        jdbcTemplate.update("UPDATE pedidos SET fecha_creacion = ? WHERE id = ?", LocalDateTime.now().minusDays(2), pedidoId);
    }

    private JobParameters parametros() {
        return new JobParametersBuilder()
                .addString("fechaCorte", LocalDateTime.now().minusHours(24).toString())
                .addLong("corrida", System.currentTimeMillis())
                .toJobParameters();
    }

    @Test
    void soloExpiranLosCreadosViejos() throws Exception {
        UUID reservaId = UUID.randomUUID();
        Pedido pedidoViejo = crearPedido(reservaId);
        envejecer(pedidoViejo.getId());

        Pedido pedidoNuevo = crearPedido(UUID.randomUUID());

        Pedido pedidoConfirmado = crearPedido(UUID.randomUUID());
        pedidoConfirmado.cambiarEstado(EstadoPedido.CONFIRMADO);
        pedidoRepository.save(pedidoConfirmado);
        envejecer(pedidoConfirmado.getId());

        JobExecution corrida = jobLauncherTestUtils.launchJob(parametros());

        assertThat(corrida.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(pedidoRepository.findById(pedidoViejo.getId()).orElseThrow().getEstado())
                .isEqualTo(EstadoPedido.EXPIRADO);
        assertThat(pedidoRepository.findById(pedidoNuevo.getId()).orElseThrow().getEstado())
                .isEqualTo(EstadoPedido.CREADO);
        assertThat(pedidoRepository.findById(pedidoConfirmado.getId()).orElseThrow().getEstado())
                .isEqualTo(EstadoPedido.CONFIRMADO);

        verify(inventarioClient).liberar(reservaId);
        verifyNoMoreInteractions(inventarioClient);

        List<ResumenExpiracion> resumenes = resumenExpiracionRepository.findAll();
        assertThat(resumenes).hasSize(1);
        assertThat(resumenes.get(0).getExpirados()).isEqualTo(1);
    }

    @Test
    void inventarioCaidoSalteaElPedidoYSigue() throws Exception {
        UUID reservaId = UUID.randomUUID();
        UUID reservaFalladaId = UUID.randomUUID();
        Pedido pedidoOk = crearPedido(reservaId);
        Pedido pedidoFallado = crearPedido(reservaFalladaId);
        envejecer(pedidoOk.getId());
        envejecer(pedidoFallado.getId());

        doThrow(new ServicioNoDisponibleException("Servicio no disponible"))
                .when(inventarioClient).liberar(reservaFalladaId);

        JobExecution corrida = jobLauncherTestUtils.launchJob(parametros());

        assertThat(corrida.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(pedidoRepository.findById(pedidoOk.getId()).orElseThrow().getEstado())
                .isEqualTo(EstadoPedido.EXPIRADO);
        assertThat(pedidoRepository.findById(pedidoFallado.getId()).orElseThrow().getEstado())
                .isEqualTo(EstadoPedido.CREADO);

        ResumenExpiracion resumen = resumenExpiracionRepository.findAll().get(0);
        assertThat(resumen.getExpirados()).isEqualTo(1);
        assertThat(resumen.getSalteados()).isEqualTo(1);
    }
}
