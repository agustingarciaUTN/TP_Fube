package com.tiendaonline.clientes.mappers;

import com.tiendaonline.clientes.DTOs.ClienteDTO;
import com.tiendaonline.clientes.modelo.Cliente;

public class ClienteMapper {
    public static ClienteDTO toDTO(Cliente cliente){
        return ClienteDTO.builder()
                .id(cliente.getId())
                .nombre(cliente.getNombre())
                .email(cliente.getEmail())
                .fechaAlta(cliente.getFechaAlta())
                .build();
    }
    public static Cliente toEntity(ClienteDTO cliente){
        return Cliente.builder()
                .id(cliente.getId())
                .nombre(cliente.getNombre())
                .email(cliente.getEmail())
                .fechaAlta(cliente.getFechaAlta())
                .build();
    }
}
