package com.tiendaonline.clientes.DTOs;

import lombok.Builder;

import java.util.List;

@Builder
public record ListaPedidosResponseDTO(List<PedidoResponseDTO> pedidos, long total) {
    public List<PedidoResponseDTO> getPedidos(){
        return pedidos;
    }
}
