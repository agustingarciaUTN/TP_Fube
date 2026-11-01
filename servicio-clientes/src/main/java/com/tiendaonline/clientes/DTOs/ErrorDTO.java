package com.tiendaonline.clientes.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ErrorDTO { //Clase creada para ser retornada por el RestExceptionHandler
    private int codError;
    private String error;
    private String mensaje;
}
