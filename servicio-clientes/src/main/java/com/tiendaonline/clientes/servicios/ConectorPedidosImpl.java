package com.tiendaonline.clientes.servicios;

import com.tiendaonline.clientes.DTOs.ListaPedidosResponseDTO;
import com.tiendaonline.clientes.excepciones.ClienteNoEncontradoException;
import com.tiendaonline.clientes.excepciones.ServicioExternoNoDisponibleException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
public class ConectorPedidosImpl implements ConectorPedidos {
    private final RestClient restClient;

    public ConectorPedidosImpl(@Value("${servicios.pedidos.url}") String pedidosBaseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(pedidosBaseUrl)
                .build();
    }

    public ListaPedidosResponseDTO listarPedidosCliente(UUID clienteId) {
        try{
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/pedidos")
                            .queryParam("clienteId", clienteId)
                            .build())
                    .retrieve()
                    .body(ListaPedidosResponseDTO.class);
        }catch (HttpClientErrorException.NotFound ex){
            throw new ClienteNoEncontradoException(ex.getMessage());
        } catch (ResourceAccessException | HttpServerErrorException e) {
            throw new ServicioExternoNoDisponibleException("El servicio de pedidos no está disponible");
        }
    }
}
