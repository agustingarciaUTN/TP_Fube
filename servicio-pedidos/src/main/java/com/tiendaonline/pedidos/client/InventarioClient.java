package com.tiendaonline.pedidos.client;

import com.tiendaonline.pedidos.dominio.ProductoNoEncontradoException;
import com.tiendaonline.pedidos.dominio.StockInsuficienteException;

import java.util.UUID;


public interface InventarioClient {
    /** Metodo que reserva una cantidad de producto del stock y lo asigna a cierto Pedido
     * @throws ProductoNoEncontradoException si el producto no existe
     * @throws StockInsuficienteException si no alcanza el stock
     * @throws ServicioNoDisponibleException si inventario no responde */
    ReservaConfirmada reservar(UUID productoId, int cantidad, UUID pedidoId);

    /** Metodo que libera stock que habia sido reservado
     * @throws ServicioNoDisponibleException si inventario no responde */
    void liberar(UUID reservaId);

}
