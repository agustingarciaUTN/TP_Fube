package com.tiendaonline.pedidos.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
public class ClientesClientHttp implements ClientesClient {

    private final RestClient restClient;

    public ClientesClientHttp(@Qualifier("clientesRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public boolean clienteExiste(UUID id){
        try{
            restClient.get().uri("/v1/clientes/{id}", id).retrieve().toBodilessEntity();
            return true;
        } catch(HttpClientErrorException.NotFound e){
            return false;
        } catch(HttpServerErrorException | ResourceAccessException e){
            throw new ServicioNoDisponibleException("El servicio de clientes no esta disponible", e);
        }
    }
}
