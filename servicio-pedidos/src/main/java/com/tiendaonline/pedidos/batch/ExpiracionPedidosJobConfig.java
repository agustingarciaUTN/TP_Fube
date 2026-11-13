package com.tiendaonline.pedidos.batch;

import com.tiendaonline.pedidos.dominio.EstadoPedido;
import com.tiendaonline.pedidos.dominio.Pedido;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Map;

@Configuration
public class ExpiracionPedidosJobConfig {

    @Bean
    @StepScope
    public JpaCursorItemReader<Pedido> pedidoItemReader(EntityManagerFactory emf,
                                                        @Value("#{jobParameters['fechaCorte']}") String fechaCorte) {
        return new JpaCursorItemReaderBuilder<Pedido>()
                .name("lectorPedidosAExpirar")
                .entityManagerFactory(emf)
                .queryString("select p from Pedido p where p.estado = :estado and p.fechaCreacion < :fechaCorte order by p.fechaCreacion")
                .parameterValues(Map.of("estado", EstadoPedido.CREADO, "fechaCorte", LocalDateTime.parse(fechaCorte)))
                .build();
    }
}
