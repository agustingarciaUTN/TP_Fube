package com.tiendaonline.inventario.repositorios;

import com.tiendaonline.inventario.modelo.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RepositorioReserva extends JpaRepository<Reserva, UUID> {

}
