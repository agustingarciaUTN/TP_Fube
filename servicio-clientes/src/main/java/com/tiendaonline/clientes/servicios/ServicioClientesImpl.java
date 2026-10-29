package com.tiendaonline.clientes.servicios;

import com.tiendaonline.clientes.DTOs.ClienteDTO;
import com.tiendaonline.clientes.excepciones.EmailExistenteException;
import com.tiendaonline.clientes.excepciones.NombreExistenteException;
import com.tiendaonline.clientes.mappers.ClienteMapper;
import com.tiendaonline.clientes.modelo.Cliente;
import com.tiendaonline.clientes.repositorios.RepositorioClientes;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class ServicioClientesImpl extends ServicioClientes{
    private final RepositorioClientes repositorioClientes;

    public ClienteDTO registrarCliente(ClienteDTO clienteDTO){
        if (repositorioClientes.findByEmail(clienteDTO.getEmail()).isPresent()) {
            throw new EmailExistenteException("El correo electronico ya se encuentra registrado");
        }
        if (repositorioClientes.findByNombre(clienteDTO.getNombre()).isPresent()) {
            throw new NombreExistenteException("El nombre ya se encuentra registrado");
        }
        Cliente cliente = Cliente.builder()
                .nombre(clienteDTO.getNombre())
                .email(clienteDTO.getEmail())
                .fechaAlta(LocalDateTime.now())
                .build();
        return ClienteMapper.toDTO(repositorioClientes.save(cliente)) ;
    }
}
