package com.tiendaonline.pedidos.servicio;

import com.tiendaonline.pedidos.client.ClientesClient;
import com.tiendaonline.pedidos.client.InventarioClient;
import com.tiendaonline.pedidos.client.ReservaConfirmada;
import com.tiendaonline.pedidos.client.ServicioNoDisponibleException;
import com.tiendaonline.pedidos.dominio.*;
import com.tiendaonline.pedidos.dto.CrearPedidoRequest;
import com.tiendaonline.pedidos.dto.ListaPedidosResponse;
import com.tiendaonline.pedidos.dto.PedidoResponse;
import com.tiendaonline.pedidos.repositorio.PedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final ClientesClient clientesClient;
    private final InventarioClient inventarioClient;

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


    public PedidoResponse crear(CrearPedidoRequest request) {
        if(!clientesClient.clienteExiste(request.clienteId())){
            throw new ClienteNoEncontradoException("El cliente " + request.clienteId() + " no existe");
        }

        UUID pedidoId = UUID.randomUUID();

        List<ItemPedido> itemsPedido = new ArrayList<>();

        try {
            request.items().forEach(item -> {
                ReservaConfirmada reserva = inventarioClient.reservar(item.productoId(), item.cantidad(), pedidoId);
                ItemPedido itemPedido = new ItemPedido(item.cantidad(), reserva.precioUnitario(), item.productoId(), reserva.reservaId());
                itemsPedido.add(itemPedido);
            });
        } catch (RuntimeException e) {
            for (ItemPedido itemPedido : itemsPedido) {
                try {
                    inventarioClient.liberar(itemPedido.getReservaId());
                } catch (ServicioNoDisponibleException ex) {
                    log.warn("No se pudo liberar la reserva {} del pedido {}",  itemPedido.getReservaId(), pedidoId);
                }
            }
            throw e;
        }
        Pedido pedido = new Pedido(request.clienteId(), itemsPedido, pedidoId);
        pedidoRepository.save(pedido);
        return PedidoResponse.desde(pedido);
    }

    public PedidoResponse cambiarEstado(UUID id, EstadoPedido estadoFinal) {
        Pedido pedido = pedidoRepository.findById(id).orElseThrow(() -> new PedidoNoEncontradoException("El pedido indicado no existe"));

        if (estadoFinal == EstadoPedido.EXPIRADO){
            throw new TransicionInvalidaException("Un pedido no puede pasar a EXPIRADO desde la API, ese estado lo asigna solo un proceso automatico");
        }

        pedido.cambiarEstado(estadoFinal);

        if (estadoFinal == EstadoPedido.CANCELADO){
            for(ItemPedido item : pedido.getItems()){
                inventarioClient.liberar(item.getReservaId());
            }
        }

        pedidoRepository.save(pedido);
        return PedidoResponse.desde(pedido);
    }
}
