package com.tiendaonline.clientes.servicios;

import com.tiendaonline.clientes.DTOs.ClienteDTO;
import com.tiendaonline.clientes.excepciones.NombreInvalidoException;
import com.tiendaonline.clientes.excepciones.EmailExistenteException;
import com.tiendaonline.clientes.excepciones.EmailInvalidoException;
import com.tiendaonline.clientes.excepciones.NombreExistenteException;
import com.tiendaonline.clientes.mappers.ClienteMapper;
import com.tiendaonline.clientes.modelo.Cliente;
import com.tiendaonline.clientes.repositorios.RepositorioClientes;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

import static java.util.Objects.isNull;

@Service
@AllArgsConstructor
public class ServicioClientesImpl extends ServicioClientes{
    private final RepositorioClientes repositorioClientes;
    private static final Pattern PATRON_NOMBRE = Pattern.compile(
            "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]{3,}$"
    );

    private static final Pattern PATRON_EMAIL = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    public ClienteDTO registrarCliente(ClienteDTO clienteDTO){
        verificarDatosRegistro(clienteDTO);
        if (repositorioClientes.existsByEmail(clienteDTO.getEmail())) {
            throw new EmailExistenteException("El correo electronico ya se encuentra registrado");
        }
        if (repositorioClientes.existsByNombre(clienteDTO.getNombre())) {
            throw new NombreExistenteException("El nombre ya se encuentra registrado");
        }
        Cliente cliente = Cliente.builder()
                .nombre(clienteDTO.getNombre())
                .email(clienteDTO.getEmail())
                .fechaAlta(LocalDateTime.now())
                .build();
        return ClienteMapper.toDTO(repositorioClientes.save(cliente)) ;
    }

    private void verificarDatosRegistro(ClienteDTO clienteDTO){
        if (isNull(clienteDTO.getNombre())||
                !PATRON_NOMBRE.matcher(clienteDTO.getNombre().trim()).matches()) {
            throw new NombreInvalidoException("El nombre no es valido");
        }
        if (isNull(clienteDTO.getEmail()) ||
                !PATRON_EMAIL.matcher(clienteDTO.getEmail()).matches()) {
            throw new EmailInvalidoException("El correo electronico no es valido");
        }
    }

}
