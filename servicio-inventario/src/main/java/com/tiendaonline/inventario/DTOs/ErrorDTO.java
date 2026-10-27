package com.tiendaonline.inventario.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ErrorDTO { //Clase creada para ser retornada por el RestExceptionHandler
    private String error;
    private String mensaje;
}
