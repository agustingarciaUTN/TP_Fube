package com.tiendaonline.pedidos.dto;

import com.tiendaonline.pedidos.dominio.Pedido;

import java.util.List;

public record ListaPedidosResponse(List<PedidoResponse> items, long total) {
    public static ListaPedidosResponse desde(List<Pedido> pedidos) {

        List<PedidoResponse> listaTemporal = pedidos.stream()
                .map(PedidoResponse::desde).toList();

        return new ListaPedidosResponse(listaTemporal, listaTemporal.size());

    }
}
