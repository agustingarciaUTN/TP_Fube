package com.tiendaonline.clientes.servicios;

import com.tiendaonline.clientes.DTOs.ClienteDTO;
import com.tiendaonline.clientes.excepciones.NombreInvalidoException;
import com.tiendaonline.clientes.excepciones.EmailExistenteException;
import com.tiendaonline.clientes.excepciones.EmailInvalidoException;
import com.tiendaonline.clientes.excepciones.NombreExistenteException;
import com.tiendaonline.clientes.modelo.Cliente;
import com.tiendaonline.clientes.repositorios.RepositorioClientes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;

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

    private ClienteDTO ejClienteDTO() {
        return ClienteDTO.builder()
                .nombre("JuanPerez")
                .email("juanPerez@gmail.com")
                .build();
    }

    private Cliente ejClienteEntity() {
        return Cliente.builder()
                .nombre("JuanPerez")
                .email("juanPerez@gmail.com")
                .build();
    }

    @Test
    void registrarCliente_Correcto() {
        ClienteDTO dto = ejClienteDTO();
        given(repositorioClientes.existsByEmail(dto.getEmail()))
                .willReturn(false);
        given(repositorioClientes.existsByNombre(dto.getNombre()))
                .willReturn(false);
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
    void registrarCliente_Fallo_EmailExistente() {
        ClienteDTO dto = ejClienteDTO();
        Cliente clienteEncontrado = ejClienteEntity();
        given(repositorioClientes.existsByEmail(dto.getEmail()))
                .willReturn(true);

        assertThrows(
                EmailExistenteException.class,
                () -> servicioClientes.registrarCliente(dto)
        );

        verify(repositorioClientes, never()).save(any(Cliente.class));
        verify(repositorioClientes, times(1))
                .existsByEmail(dto.getEmail());
    }

    @Test
    void registrarCliente_Fallo_NombreExistente(){
        ClienteDTO dto = ejClienteDTO();
        Cliente clienteEncontrado = ejClienteEntity();
        given(repositorioClientes.existsByEmail(dto.getEmail()))
                .willReturn(false);
        given(repositorioClientes.existsByNombre(dto.getNombre()))
                .willReturn(true);

        assertThrows(
                NombreExistenteException.class,
                () -> servicioClientes.registrarCliente(dto)
        );

        verify(repositorioClientes, never()).save(any(Cliente.class));
        verify(repositorioClientes, times(1))
                .existsByEmail(dto.getEmail());
        verify(repositorioClientes, times(1))
                .existsByNombre(dto.getNombre());
    }

    @ParameterizedTest //Prueba diferentes entradas que tienen que dar mismo error
    @NullSource //Si el nombre es null
    @ValueSource(strings = {
            "a", //Muy corto
            "Juan1", //Numero
            "Juan_Perez", //Simbolo
            "Juan/nPerez", //Salto de linea
            "", //Vacio
            " " //Solo espacio
    })
    void registrarCliente_Fallo_NombreInvalido(String nombre){
        ClienteDTO dto = ClienteDTO.builder().nombre(nombre).email("juanPerez@gmail.com").build();

        NombreInvalidoException exepcion = assertThrows(
                NombreInvalidoException.class,
                () -> servicioClientes.registrarCliente(dto)
        );

        assertEquals("El nombre no es valido", exepcion.getMessage());
        verify(repositorioClientes, never()).save(any(Cliente.class));
        verify(repositorioClientes, never())
                .existsByEmail(any(String.class));
        verify(repositorioClientes, never())
                .existsByNombre(any(String.class));
    }

    @ParameterizedTest //Prueba diferentes entradas que tienen que dar mismo error
    @NullSource //Si el nombre es null
    @ValueSource(strings = {
            "a.com", //Falta '@'
            "", //Vacio
            " ", //Solo espacio
            "@", //Solo '@'
            "a@b", //Falta ".[string]"
            "a@b.c9m", //Numero despues del punto
            "a^b@c.com", //Caracter no permitido ('^')
    })
    void registrarCliente_Fallo_EmailInvalido(String email){
        ClienteDTO dto = ClienteDTO.builder().nombre("juanPerez").email(email).build();

        EmailInvalidoException exepcion = assertThrows(
                EmailInvalidoException.class,
                () -> servicioClientes.registrarCliente(dto)
        );

        assertEquals("El correo electronico no es valido", exepcion.getMessage());
        verify(repositorioClientes, never()).save(any(Cliente.class));
        verify(repositorioClientes, never())
                .existsByEmail(any(String.class));
        verify(repositorioClientes, never())
                .existsByNombre(any(String.class));
    }

}
