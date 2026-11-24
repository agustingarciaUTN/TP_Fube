package com.tiendaonline.pedidos.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class ClientsHttpConfig {

    @Value("${servicios.timeout-conexion}")
    private Duration timeoutConexion;

    @Value("${servicios.timeout-lectura}")
    private Duration timeoutLectura;


    @Bean
    RestClient clientesRestClient(RestClient.Builder builder,
                                  @Value("${servicios.clientes.url}") String url) {
        return builder.baseUrl(url).requestFactory(fabricaConTimeouts()).build();
    }

    @Bean
    RestClient inventarioRestClient(RestClient.Builder builder,
                                    @Value("${servicios.inventario.url}") String url) {
        return builder.baseUrl(url).requestFactory(fabricaConTimeouts()).build();
    }

    private ClientHttpRequestFactory fabricaConTimeouts() {
        ClientHttpRequestFactorySettings configuracion = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(timeoutConexion)
                .withReadTimeout(timeoutLectura);
        return ClientHttpRequestFactories.get(JdkClientHttpRequestFactory.class, configuracion);
    }

}
