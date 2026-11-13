package com.tiendaonline.pedidos.dto;

import com.tiendaonline.pedidos.dominio.EstadoPedido;
import jakarta.validation.constraints.NotNull;

public record CambiarEstadoRequest(@NotNull EstadoPedido estado) {
}
