package com.tiendaonline.pedidos.dto;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;

public record EjecucionExpiracionResponse(Long ejecucionId, String estado, long leidos, long expirados, long salteados) {

    public static EjecucionExpiracionResponse desde(JobExecution job){
        StepExecution step = job.getStepExecutions().iterator().next();
        return new EjecucionExpiracionResponse(job.getId(), job.getStatus().name(),
                step.getReadCount(),
                step.getWriteCount(),
                step.getSkipCount());
    }
}
