package com.tiendaonline.clientes.statsig;

import com.statsig.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutionException;

@Configuration
public class StatsigConfig {

    @Value("${statsig.secret-key}")
    private String secretKey;

    @Bean(destroyMethod = "shutdown")
    public Statsig statsigServer() throws Exception {
        StatsigOptions options = new StatsigOptions.Builder().build();

        Statsig myStatsigServer = new Statsig(secretKey, options);
        myStatsigServer.initialize().get();

        return myStatsigServer;
    }
}