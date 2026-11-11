package com.tiendaonline.clientes.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
@Schema(
        name = "ErrorDTO",
        description = "Respuesta del EndPoint cuando se encuentra un error"
)
public class ErrorDTO { //Clase creada para ser retornada por el RestExceptionHandler
    //@Schema es para documentar cada atributo en swagger
    @Schema(description = "Codigo de error", example = "000")
    private int codError;
    @Schema(description = "Texto con el tipo de error", example = "TIPO_ERROR")
    private String error;
    @Schema(description = "Texto con el mensaje del error", example = "Descripción del error ocurrido")
    private String mensaje;
}
