package com.tiendaonline.pedidos.repositorio;

import com.tiendaonline.pedidos.dominio.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PedidoRepository extends JpaRepository<Pedido, UUID> {
    /** Metodo que encuentra los pedidos guardados en base de datos de un cliente
     *
     * @param clienteId
     * @return Lista de pedidos
     */
    List<Pedido> findByClienteId(UUID clienteId);
}
