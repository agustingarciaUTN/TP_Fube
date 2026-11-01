package com.tiendaonline.pedidos.client;

import java.math.BigDecimal;
import java.util.UUID;

public record ReservaConfirmada(UUID reservaId, BigDecimal precioUnitario) {}
