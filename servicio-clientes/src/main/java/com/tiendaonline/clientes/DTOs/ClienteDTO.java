package com.tiendaonline.clientes.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClienteDTO {
    //@Schema es para documentar cada atributo en swagger
    @Schema(description = "Identificador único asignado automáticamente", example = "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d", accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;
    @Schema(description = "Nombre completo del cliente", example = "Juan Perez", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;
    @Schema(description = "Correo electrónico del cliente", example = "juan@perez.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;
    @Schema(description = "Fecha de alta en el sistema", example = "2026-11-03T18:30:01", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime fechaAlta;
}
