package com.tiendaonline.inventario.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="reservas")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;
    @Column(name="producto_id")
    private UUID productoId;
    @Column(name="pedido_id")
    private UUID pedidoId;
    private int cantidad;
    private String estado;
    @Column(name="fecha_creacion")
    private LocalDateTime fechaCreacion;
}
