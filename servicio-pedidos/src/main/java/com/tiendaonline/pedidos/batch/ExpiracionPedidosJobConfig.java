package com.tiendaonline.pedidos.batch;

import com.tiendaonline.pedidos.client.ServicioNoDisponibleException;
import com.tiendaonline.pedidos.dominio.EstadoPedido;
import com.tiendaonline.pedidos.dominio.Pedido;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.Map;

@Configuration
public class ExpiracionPedidosJobConfig {

    @Bean
    @StepScope
    public JpaCursorItemReader<Pedido> expirarPedidoReader(EntityManagerFactory emf,
                                                           @Value("#{jobParameters['fechaCorte']}") String fechaCorte) {
        return new JpaCursorItemReaderBuilder<Pedido>()
                .name("lectorPedidosAExpirar")
                .entityManagerFactory(emf)
                .queryString("select p from Pedido p where p.estado = :estado and p.fechaCreacion < :fechaCorte order by p.fechaCreacion")
                .parameterValues(Map.of("estado", EstadoPedido.CREADO, "fechaCorte", LocalDateTime.parse(fechaCorte)))
                .build();
    }

    @Bean
    public Step expirarPedidosStep(JobRepository repository, PlatformTransactionManager transactionManager,
                               JpaCursorItemReader<Pedido> reader,  ExpiracionPedidoProcessor processor,
                               ExpiracionPedidoWriter writer){
        return new StepBuilder("expirarPedidosStep", repository)
                .<Pedido,Pedido>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .skip(ServicioNoDisponibleException.class)
                .skipLimit(10)
                .processorNonTransactional()
                .build();
    }

    @Bean
    public Job expirarPedidosJob(JobRepository repository, Step expirarPedidosStep) {
        return new JobBuilder("expirarPedidosJob", repository).start(expirarPedidosStep).build();
    }
}
