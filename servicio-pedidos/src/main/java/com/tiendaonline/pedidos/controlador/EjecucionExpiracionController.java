package com.tiendaonline.pedidos.controlador;

import com.tiendaonline.pedidos.batch.LanzadorExpiracion;
import com.tiendaonline.pedidos.dto.EjecucionExpiracionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/ejecuciones-expiracion")
@RequiredArgsConstructor
public class EjecucionExpiracionController {
    private final LanzadorExpiracion lanzadorExpiracion;

    @PostMapping
    public ResponseEntity<EjecucionExpiracionResponse> lanzarExpiracion(){
        JobExecution corrida = lanzadorExpiracion.lanzar();
        EjecucionExpiracionResponse response = EjecucionExpiracionResponse.desde(corrida);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
