package com.tiendaonline.pedidos.client;

import java.util.UUID;

public record SolicitudReserva(UUID productoId, UUID pedidoId, int cantidad) {

}
