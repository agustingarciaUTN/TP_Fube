package com.tiendaonline.inventario.Modelo;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "productos")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String nombre;
    private String sku;
    @Column(name = "cantidad_disponible")
    private int cantidadDisponible;
    @Column(name = "precio_unitario")
    private float precioUnitario;
}
