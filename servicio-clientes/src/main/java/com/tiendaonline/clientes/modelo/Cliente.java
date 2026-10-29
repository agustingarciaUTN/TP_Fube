package com.tiendaonline.clientes.modelo;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "clientes")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String nombre;
    private String email;
    @Column(name = "fecha_alta")
    private LocalDateTime fechaAlta;

}
