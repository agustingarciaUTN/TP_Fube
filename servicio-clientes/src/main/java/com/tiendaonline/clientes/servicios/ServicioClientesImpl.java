package com.tiendaonline.clientes.servicios;

import com.tiendaonline.clientes.DTOs.ClienteDTO;
import com.tiendaonline.clientes.excepciones.*;
import com.tiendaonline.clientes.mappers.ClienteMapper;
import com.tiendaonline.clientes.modelo.Cliente;
import com.tiendaonline.clientes.repositorios.RepositorioClientes;
import com.tiendaonline.clientes.statsig.GestorFeatureFlagImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import java.util.UUID;
import java.util.regex.Pattern;

import static java.util.Objects.isNull;

@Service
@AllArgsConstructor
public class ServicioClientesImpl extends ServicioClientes{
    private final RepositorioClientes repositorioClientes;
    private final GestorFeatureFlagImpl gestorFeatureFlag;

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

    public ClienteDTO consultarClientePorId(UUID id){
        Cliente clienteEncontrado = repositorioClientes.findById(id)
                .orElseThrow(() -> new ClienteNoEncontradoException("No se encontró el cliente con el id introducido"));
        ClienteDTO clienteDTO = ClienteMapper.toDTO(clienteEncontrado);
        if(gestorFeatureFlag.isFlagActivo("mask-pii")){
            clienteDTO.setNombre(enmascararNombre(clienteDTO.getNombre()));
            clienteDTO.setEmail(enmascararEmail(clienteEncontrado.getEmail()));
            return clienteDTO;
        }else{
            return clienteDTO;
        }
    }
    private String enmascararNombre(String nombre){
        //Separa el string por los espacios
        String[] palabras = nombre.trim().split("\\s+");
        StringBuilder nombreEnmascarado = new StringBuilder();
        //Recorre cada string
        for (int i = 0; i < palabras.length; i++) {
            String palabra = palabras[i];
            if (!palabra.isEmpty()) {
                //Toma la primer letra, le concatena "***"
                nombreEnmascarado.append(palabra.charAt(0)).append("***");
                if (i < palabras.length - 1) {
                    //Si no es la última palabra le concatena un espacio
                    nombreEnmascarado.append(" ");
                }
            }
        }
        return nombreEnmascarado.toString();
    }
    private String enmascararEmail(String email){
        //Busca el ultimo punto
        int ultimoPuntoIndex = email.lastIndexOf('.');
        //Concatena "****@****" con lo que hay luego del ultimo punto
        return "****@****" + email.substring(ultimoPuntoIndex);

    }
}
