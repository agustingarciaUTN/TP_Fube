package com.tiendaonline.pedidos.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record ItemPedidoRequest(@NotNull UUID productoId, @NotNull @Positive Integer cantidad) {
}
