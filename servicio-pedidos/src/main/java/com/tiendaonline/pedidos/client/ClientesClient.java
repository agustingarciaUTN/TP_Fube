package com.tiendaonline.pedidos.client;

import java.util.UUID;

public interface ClientesClient {
    /** Metodo que revisa si el cliente existe recibiendo un clienteId y devolviendo true/false
     @throws ServicioNoDisponibleException si clientes no responde */
    boolean clienteExiste(UUID clienteId);
}
