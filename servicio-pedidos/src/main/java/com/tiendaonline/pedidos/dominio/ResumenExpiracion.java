package com.tiendaonline.pedidos.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "resumenes_expiracion")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResumenExpiracion {
    @Id
    private UUID id = UUID.randomUUID();

    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private LocalDateTime fechaCorte;

    private int leidos;
    private int expirados;
    private int salteados;

    private String estado;

    public ResumenExpiracion(LocalDateTime fechaInicio, LocalDateTime fechaFin, LocalDateTime fechaCorte,
                             int leidos, int expirados, int salteados, String estado) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.fechaCorte = fechaCorte;
        this.leidos = leidos;
        this.expirados = expirados;
        this.salteados = salteados;
        this.estado = estado;
    }
}
