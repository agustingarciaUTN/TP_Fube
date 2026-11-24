package com.tiendaonline.pedidos.controlador;

import com.tiendaonline.pedidos.batch.LanzadorExpiracion;
import com.tiendaonline.pedidos.dto.EjecucionExpiracionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Batch de expiracion",
        description = "Ejecucion del job que expira pedidos")
public class EjecucionExpiracionController {
    private final LanzadorExpiracion lanzadorExpiracion;

    @PostMapping
    @Operation(summary = "Ejecutar la expiracion de pedidos",
            description = "Corre el mismo job que el cron nocturno: pasa a EXPIRADO los pedidos CREADO con mas de 24 h," +
                    " libera sus reservas y guarda un resumen de la corrida. Responde cuando la corrida termino")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Corrida ejecutada; devuelve su id, el estado y los contadores")
    })
    public ResponseEntity<EjecucionExpiracionResponse> lanzarExpiracion(){
        JobExecution corrida = lanzadorExpiracion.lanzar();
        EjecucionExpiracionResponse response = EjecucionExpiracionResponse.desde(corrida);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
