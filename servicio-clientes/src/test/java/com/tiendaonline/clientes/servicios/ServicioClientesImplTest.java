package com.tiendaonline.clientes.servicios;

import com.tiendaonline.clientes.DTOs.ClienteDTO;
import com.tiendaonline.clientes.excepciones.EmailExistenteException;
import com.tiendaonline.clientes.excepciones.NombreExistenteException;
import com.tiendaonline.clientes.modelo.Cliente;
import com.tiendaonline.clientes.repositorios.RepositorioClientes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServicioClientesImplTest {
    @Mock
    RepositorioClientes repositorioClientes;

    @InjectMocks
    ServicioClientesImpl servicioClientes;

    private ClienteDTO ejClienteDTO(){
        return ClienteDTO.builder()
                .nombre("JuanPerez")
                .email("juanPerez@gmail.com")
                .build();
    }
    private Cliente ejClienteEntity(){
        return Cliente.builder()
                .nombre("JuanPerez")
                .email("juanPerez@gmail.com")
                .build();
    }

    @Test
    void registrarCliente_Correcto(){
        ClienteDTO dto = ejClienteDTO();
        given(repositorioClientes.findByEmail(dto.getEmail()))
                .willReturn(Optional.empty());
        given(repositorioClientes.findByNombre(dto.getNombre()))
                .willReturn(Optional.empty());
        given(repositorioClientes.save(any(Cliente.class)))
                .willAnswer(invocation -> invocation.getArgument(0));


        ClienteDTO resultado = servicioClientes.registrarCliente(dto);

        assertNotNull(resultado);
        assertEquals(dto.getNombre(), resultado.getNombre());
        assertEquals(dto.getEmail(), resultado.getEmail());

        ArgumentCaptor<Cliente> captor = ArgumentCaptor.forClass(Cliente.class);
        verify(repositorioClientes, times(1)).save(captor.capture());
        Cliente clienteGuardado = captor.getValue();

        assertEquals(dto.getNombre(), clienteGuardado.getNombre());
        assertEquals(dto.getEmail(), clienteGuardado.getEmail());
        assertNotNull(clienteGuardado.getFechaAlta());
    }

    @Test
    void registrarCliente_Fallo_EmailExistente(){
        ClienteDTO dto = ejClienteDTO();
        Cliente clienteEncontrado = ejClienteEntity();
        given(repositorioClientes.findByEmail(dto.getEmail()))
                .willReturn(Optional.of(clienteEncontrado));

        assertThrows(
                EmailExistenteException.class,
                () -> servicioClientes.registrarCliente(dto)
        );

        verify(repositorioClientes, never()).save(any(Cliente.class));
        verify(repositorioClientes, times(1))
                .findByEmail(dto.getEmail());
    }

    @Test
    void registrarCliente_Fallo_NombreExistente(){
        ClienteDTO dto = ejClienteDTO();
        Cliente clienteEncontrado = ejClienteEntity();
        given(repositorioClientes.findByEmail(dto.getEmail()))
                .willReturn(Optional.empty());
        given(repositorioClientes.findByNombre(dto.getNombre()))
                .willReturn(Optional.of(clienteEncontrado));

        assertThrows(
                NombreExistenteException.class,
                () -> servicioClientes.registrarCliente(dto)
        );

        verify(repositorioClientes, never()).save(any(Cliente.class));
        verify(repositorioClientes, times(1))
                .findByEmail(dto.getEmail());
        verify(repositorioClientes, times(1))
                .findByNombre(dto.getNombre());
    }

    @Test
    void registrarCliente_CorrectoFINAL(){
        ClienteDTO dto = ejClienteDTO();
        given(repositorioClientes.findByEmail(dto.getEmail())).willReturn(Optional.empty());
        given(repositorioClientes.findByNombre(dto.getNombre())).willReturn(Optional.empty());
        given(repositorioClientes.save(any(Cliente.class))).willAnswer(invocation -> invocation.getArgument(0));

        ClienteDTO resultado = servicioClientes.registrarCliente(dto);

        verify(repositorioClientes, times(1)).findByEmail(dto.getEmail());
        verify(repositorioClientes, times(1)).findByNombre(dto.getNombre());

        ArgumentCaptor<Cliente> captor = ArgumentCaptor.forClass(Cliente.class);
        verify(repositorioClientes, times(1)).save(captor.capture());
        Cliente clienteGuardado = captor.getValue();

        assertEquals(dto.getNombre(), clienteGuardado.getNombre());
        assertEquals(dto.getEmail(), clienteGuardado.getEmail());

        assertEquals(dto.getNombre(), resultado.getNombre());
        assertEquals(dto.getEmail(), resultado.getEmail());
    }



}
