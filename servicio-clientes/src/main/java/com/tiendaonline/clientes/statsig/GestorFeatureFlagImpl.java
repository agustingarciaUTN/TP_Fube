package com.tiendaonline.clientes.statsig;

import com.statsig.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class GestorFeatureFlagImpl implements GestorFeatureFlag{
    private final Statsig statsig;

    @Override
    public boolean isFlagActivo(String flagName) {
        StatsigUser usuario = new StatsigUser.Builder()
                .setUserID(LocalDateTime.now().toString())
                .build();

        return statsig.checkGate(usuario, flagName);
    }
}
