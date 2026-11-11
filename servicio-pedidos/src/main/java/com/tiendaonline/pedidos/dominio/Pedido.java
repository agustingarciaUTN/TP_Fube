package com.tiendaonline.pedidos.dominio;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "pedidos")
public class Pedido {


    @Enumerated(EnumType.STRING)
    private EstadoPedido estado;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "pedido_id", nullable = false)
    private List<ItemPedido> items;

    @Id
    private UUID id;

    private UUID clienteId;
    private BigDecimal montoTotal;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;


    public Pedido(UUID clienteId, List<ItemPedido> items) {

        if (clienteId == null) {
            throw new IllegalArgumentException("Un pedido debe tener asignado un cliente");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Un pedido debe tener al menos un item asignado.");
        }

        LocalDateTime fecha = LocalDateTime.now();
        this.clienteId = clienteId;
        this.estado = EstadoPedido.CREADO;
        this.items = new ArrayList<>(items);
        this.fechaCreacion = fecha;
        this.fechaActualizacion = fecha;
        this.id = UUID.randomUUID();
        this.montoTotal = calcularMontoTotal();
    }

    private BigDecimal calcularMontoTotal() {
        return items.stream().map(ItemPedido::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void cambiarEstado(EstadoPedido estadoDestino) {
        if (!estado.transicionValida(estadoDestino)) {
            throw new TransicionInvalidaException("Un pedido no puede estar " + this.estado + " y luego ser " + estadoDestino);
        }

        this.estado = estadoDestino;
        this.fechaActualizacion = LocalDateTime.now();
    }

}
