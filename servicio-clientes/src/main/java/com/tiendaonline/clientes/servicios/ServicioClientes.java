package com.tiendaonline.clientes.servicios;

import com.tiendaonline.clientes.DTOs.ClienteDTO;
import com.tiendaonline.clientes.DTOs.ListaPedidosResponseDTO;

import java.util.UUID;

public interface ServicioClientes {
    ClienteDTO registrarCliente(ClienteDTO clienteDTO);

    void verificarDatosRegistro(ClienteDTO clienteDTO);

    ClienteDTO consultarClientePorId(UUID id);

    ListaPedidosResponseDTO listarPedidosCliente(UUID idCliente);
}
