package com.tiendaonline.clientes.statsig;

public interface GestorFeatureFlag {
    boolean isFlagActivo(String flagName);
}
