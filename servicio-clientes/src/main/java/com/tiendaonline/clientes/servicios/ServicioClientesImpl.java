package com.tiendaonline.clientes.servicios;

import com.tiendaonline.clientes.DTOs.ClienteDTO;
import com.tiendaonline.clientes.DTOs.ListaPedidosResponseDTO;
import com.tiendaonline.clientes.excepciones.*;
import com.tiendaonline.clientes.mappers.ClienteMapper;
import com.tiendaonline.clientes.modelo.Cliente;
import com.tiendaonline.clientes.repositorios.RepositorioClientes;
import com.tiendaonline.clientes.statsig.GestorFeatureFlagImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

import java.util.UUID;
import java.util.regex.Pattern;

import static java.util.Objects.isNull;

@Service
@AllArgsConstructor
@Slf4j
public class ServicioClientesImpl implements ServicioClientes{
    private final RepositorioClientes repositorioClientes;
    private final GestorFeatureFlagImpl gestorFeatureFlag;
    private final ConectorPedidosImpl servicioPedidos;

    //Patron para el nombre
    private static final Pattern PATRON_NOMBRE = Pattern.compile(
            "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]{3,}$"
    );
    //Patron para el email
    private static final Pattern PATRON_EMAIL = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    @Override
    public ClienteDTO registrarCliente(ClienteDTO clienteDTO){
        log.info("Inicia registro del cliente");
        //Se llama al metodo para simplificar
        verificarDatosRegistro(clienteDTO);
        if (repositorioClientes.existsByEmail(clienteDTO.getEmail())) {
            log.warn("El email {} ya está registrado", clienteDTO.getEmail());
            throw new EmailExistenteException("El correo electronico ya se encuentra registrado");
        }
        if (repositorioClientes.existsByNombre(clienteDTO.getNombre())) {
            log.warn("El nombre {} ya está registrado", clienteDTO.getNombre());
            throw new NombreExistenteException("El nombre ya se encuentra registrado");
        }
        //Se genera el cliente para guardarse
        Cliente cliente = Cliente.builder()
                .nombre(clienteDTO.getNombre())
                .email(clienteDTO.getEmail())
                .fechaAlta(LocalDateTime.now())
                .build();
        //Se guarda el cliente
        ClienteDTO clienteGuardado = ClienteMapper.toDTO(repositorioClientes.save(cliente));
        log.info("Se guardó el cliente con nombre {} y email {}", clienteDTO.getNombre(), clienteDTO.getEmail());
        //Se retorna el cliente guardado
        return clienteGuardado;
    }

    @Override
    public void verificarDatosRegistro(ClienteDTO clienteDTO){
        //verifica que el nombre no este vacio y que cumpla con el patron
        if (isNull(clienteDTO.getNombre())||
                !PATRON_NOMBRE.matcher(clienteDTO.getNombre().trim()).matches()) {
            throw new NombreInvalidoException("El nombre no es valido");
        }
        //Verifica que el email no este vacio y que cumpla con el patron
        if (isNull(clienteDTO.getEmail()) ||
                !PATRON_EMAIL.matcher(clienteDTO.getEmail()).matches()) {
            throw new EmailInvalidoException("El correo electronico no es valido");
        }
        log.info("Nombre {} y mail {} verificados exitosamente", clienteDTO.getNombre(), clienteDTO.getEmail());
    }

    @Override
    public ClienteDTO consultarClientePorId(UUID id){
        log.info("Buscando cliente con id: {}", id);
        //Si el cliente no existe se lanza una excepcion
        Cliente clienteEncontrado = repositorioClientes.findById(id)
                .orElseThrow(() -> new ClienteNoEncontradoException("No se encontró el cliente con el id introducido"));
        ClienteDTO clienteDTO = ClienteMapper.toDTO(clienteEncontrado);
        //Se consulta si el feature flag está activo y se realiza el enmascarado de nombre y email si es necesario
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

    @Override
    public ListaPedidosResponseDTO listarPedidosCliente(UUID idCliente){
        log.info("Consultando pedidos para el cliente con id={}", idCliente);
        ListaPedidosResponseDTO respuesta = servicioPedidos.listarPedidosCliente(idCliente);
        log.info("Pedidos recuperados con éxito para id={}. Cantidad encontrada: {}", idCliente, respuesta.total());
        return respuesta;
    }
}
