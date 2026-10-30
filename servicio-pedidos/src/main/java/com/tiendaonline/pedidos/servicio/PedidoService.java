package com.tiendaonline.pedidos.servicio;

import com.tiendaonline.pedidos.dominio.Pedido;
import com.tiendaonline.pedidos.dominio.PedidoNoEncontradoException;
import com.tiendaonline.pedidos.dto.ListaPedidosResponse;
import com.tiendaonline.pedidos.dto.PedidoResponse;
import com.tiendaonline.pedidos.repositorio.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PedidoService {
    private final PedidoRepository pedidoRepository;

    @Transactional(readOnly = true)
    public PedidoResponse obtener(UUID id) {
        Optional<Pedido> pedido = pedidoRepository.findById(id);
        return pedido.map(PedidoResponse::desde)
                .orElseThrow(() -> new PedidoNoEncontradoException("El pedido indicado no existe"));
    }

    @Transactional(readOnly = true)
    public ListaPedidosResponse listarPorCliente(UUID clienteId) {
        return ListaPedidosResponse.desde(pedidoRepository.findByClienteId(clienteId));
    }
}
