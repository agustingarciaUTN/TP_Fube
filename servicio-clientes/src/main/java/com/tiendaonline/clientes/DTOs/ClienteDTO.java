package com.tiendaonline.clientes.DTOs;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClienteDTO {
    private UUID id;
    private String nombre;
    private String email;
    private LocalDateTime fechaAlta;
}
