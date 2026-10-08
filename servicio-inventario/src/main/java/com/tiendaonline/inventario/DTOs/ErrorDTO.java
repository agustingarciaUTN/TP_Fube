package com.tiendaonline.inventario.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ErrorDTO {
    private String error;
    private String mensaje;
}
