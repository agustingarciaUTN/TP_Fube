package com.tiendaonline.pedidos.batch;

import com.tiendaonline.pedidos.dominio.ResumenExpiracion;
import com.tiendaonline.pedidos.repositorio.ResumenExpiracionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class ResumenExpiracionListener implements JobExecutionListener {

    private final ResumenExpiracionRepository resumenExpiracionRepository;

    @Override
    public void afterJob(JobExecution jobExecution) {
        StepExecution step = jobExecution.getStepExecutions().iterator().next();

        ResumenExpiracion resumen = new ResumenExpiracion(jobExecution.getStartTime(), LocalDateTime.now(),
                LocalDateTime.parse(jobExecution.getJobParameters().getString("fechaCorte")),
                (int) step.getReadCount(), (int) step.getWriteCount(), (int) step.getSkipCount(),
                jobExecution.getStatus().name());

        resumenExpiracionRepository.save(resumen);
        log.info("Resumen de expiracion guardado exitosamente: leidos={}, expirados={} y salteados={}",
                resumen.getLeidos(), resumen.getExpirados(), resumen.getSalteados());
    }
}
