CREATE TABLE resumenes_expiracion(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    fecha_inicio TIMESTAMP NOT NULL,
    fecha_fin TIMESTAMP NOT NULL,
    fecha_corte TIMESTAMP NOT NULL,
    leidos INTEGER NOT NULL,
    expirados INTEGER NOT NULL,
    salteados INTEGER NOT NULL,
    estado VARCHAR(20) NOT NULL CHECK (estado IN ('COMPLETED', 'FAILED'))
);