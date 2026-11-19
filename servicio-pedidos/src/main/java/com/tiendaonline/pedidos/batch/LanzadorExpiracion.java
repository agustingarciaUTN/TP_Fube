package com.tiendaonline.pedidos.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class LanzadorExpiracion {
    private final JobLauncher jobLauncher;
    private final Job job;

    public JobExecution lanzar() {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("fechaCorte", LocalDateTime.now().minusHours(24).toString())
                .addLong("corrida", System.currentTimeMillis())
                .toJobParameters();
        try {
            return jobLauncher.run(job, jobParameters);
        } catch (JobExecutionException e) {
            throw new RuntimeException("No se pudo lanzar la expiracion de pedidos", e);
        }
    }

    @Scheduled(cron = "${pedidos.expiracion.cron:0 0 3 * * *}")
    public void lanzarNocturno(){
        JobExecution corrida = lanzar();
        log.info("Expiracion nocturna finalizada: corrida={} con estado={}", corrida.getId(), corrida.getStatus());
    }
}
