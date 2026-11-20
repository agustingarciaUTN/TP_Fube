package com.tiendaonline.pedidos.repositorio;

import com.tiendaonline.pedidos.dominio.ResumenExpiracion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ResumenExpiracionRepository extends JpaRepository<ResumenExpiracion, UUID> {
}
