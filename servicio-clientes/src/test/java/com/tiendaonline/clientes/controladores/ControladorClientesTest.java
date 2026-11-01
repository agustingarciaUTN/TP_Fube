package com.tiendaonline.clientes.controladores;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiendaonline.clientes.DTOs.ClienteDTO;
import com.tiendaonline.clientes.excepciones.EmailInvalidoException;
import com.tiendaonline.clientes.excepciones.NombreInvalidoException;
import com.tiendaonline.clientes.excepciones.EmailExistenteException;
import com.tiendaonline.clientes.excepciones.NombreExistenteException;
import com.tiendaonline.clientes.servicios.ServicioClientesImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ControladorClientes.class)
class ControladorClientesTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean 
    private ServicioClientesImpl servicioClientes;

    private ClienteDTO clienteDTOReq(){
        return ClienteDTO.builder()
                .nombre("Juan Perez")
                .email("juan@ejemplo.com")
                .build();
    }
    private ClienteDTO clienteDTORespuesta(){
        return ClienteDTO.builder()
                .id(UUID.fromString("a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d"))
                .nombre("Juan Perez")
                .email("juan@ejemplo.com")
                .fechaAlta(LocalDateTime.now())
                .build();
    }

    @Test
    void registrarCliente_Controlador_Correcto() throws Exception {
        ClienteDTO requestDto = clienteDTOReq();
        ClienteDTO respuestaDto = clienteDTORespuesta();

        given(servicioClientes.registrarCliente(any(ClienteDTO.class)))
                .willReturn(respuestaDto);

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d"))
                .andExpect(jsonPath("$.nombre").value("Juan Perez"))
                .andExpect(jsonPath("$.email").value("juan@ejemplo.com"));
    }

    @Test
    void registrarCliente_Controlador_Fallo_EmailExistente() throws Exception {
        ClienteDTO requestDto = clienteDTOReq();

        given(servicioClientes.registrarCliente(any(ClienteDTO.class)))
                .willThrow(new EmailExistenteException("El correo electronico ya se encuentra registrado"));

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codError").value(409))
                .andExpect(jsonPath("$.error").value("CONFLICT"))
                .andExpect(jsonPath("$.mensaje")
                        .value("El correo electronico ya se encuentra registrado"));
    }

    @Test
    void registrarCliente_Controlador_Fallo_NombreExistente() throws Exception {
        ClienteDTO requestDto = clienteDTOReq();

        given(servicioClientes.registrarCliente(any(ClienteDTO.class)))
                .willThrow(new NombreExistenteException("El nombre ya se encuentra registrado"));

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codError").value(409))
                .andExpect(jsonPath("$.error").value("CONFLICT"))
                .andExpect(jsonPath("$.mensaje")
                        .value("El nombre ya se encuentra registrado"));
    }

    @Test
    void registrarCliente_Controlador_Fallo_NombreInvalido() throws Exception {
        ClienteDTO requestDto = ClienteDTO.builder()
                .nombre("").email("juan@ejemplo.com").build();

        given(servicioClientes.registrarCliente(any(ClienteDTO.class)))
                .willThrow(new NombreInvalidoException("El nombre no es valido"));

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codError").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.mensaje")
                        .value("El nombre no es valido"));
    }

    @Test
    void registrarCliente_Controlador_Fallo_EmailInvalido() throws Exception {
        ClienteDTO requestDto = ClienteDTO.builder()
                .nombre("JuanPerez").email("juan@").build();

        given(servicioClientes.registrarCliente(any(ClienteDTO.class)))
                .willThrow(new EmailInvalidoException("El correo electronico no es valido"));

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codError").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.mensaje")
                        .value("El correo electronico no es valido"));
    }

}