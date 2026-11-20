package com.tiendaonline.pedidos.batch;

import com.tiendaonline.pedidos.client.InventarioClient;
import com.tiendaonline.pedidos.dominio.ItemPedido;
import com.tiendaonline.pedidos.dominio.Pedido;
import com.tiendaonline.pedidos.repositorio.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExpiracionPedidoWriter implements ItemWriter<Pedido> {
    private final InventarioClient inventarioClient;
    private final PedidoRepository pedidoRepository;

    @Override
    public void write(@NonNull Chunk<? extends Pedido> chunk){
        for (Pedido pedido : chunk.getItems()) {
            for(ItemPedido itemPedido : pedido.getItems()){
                inventarioClient.liberar(itemPedido.getReservaId());
            }
            pedidoRepository.save(pedido);
        }
    }
}
