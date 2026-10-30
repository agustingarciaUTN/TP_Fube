package com.tiendaonline.clientes.statsig;

import com.statsig.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GestorFeatureFlagImpl implements GestorFeatureFlag{
    private final Statsig statsig;

    @Override
    public boolean isFlagActivo(String flagName) {
        StatsigUser usuario = new StatsigUser.Builder()
                .setUserID("sistema")
                .build();

        return statsig.checkGate(usuario, flagName);
    }
}
