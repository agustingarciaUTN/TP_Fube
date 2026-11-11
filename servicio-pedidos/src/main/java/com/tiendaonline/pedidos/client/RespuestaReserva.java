package com.tiendaonline.pedidos.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RespuestaReserva( UUID id) {
}
