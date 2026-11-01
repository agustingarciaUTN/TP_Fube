package com.tiendaonline.clientes.repositorios;

import com.tiendaonline.clientes.modelo.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RepositorioClientes extends JpaRepository<Cliente, UUID> {
    boolean existsByEmail(String email);
    boolean existsByNombre(String nombre);
    Optional <Cliente> findById(UUID id);
}
