package com.tiendaonline.clientes.servicios;

import com.tiendaonline.clientes.DTOs.ListaPedidosResponseDTO;

import java.util.UUID;

public interface ConectorPedidos {
    ListaPedidosResponseDTO listarPedidosCliente(UUID clienteId);
}
