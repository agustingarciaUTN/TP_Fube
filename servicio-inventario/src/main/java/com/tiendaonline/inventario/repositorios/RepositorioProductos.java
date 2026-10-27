package com.tiendaonline.inventario.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tiendaonline.inventario.modelo.Producto;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RepositorioProductos extends JpaRepository<Producto, UUID> {
}
