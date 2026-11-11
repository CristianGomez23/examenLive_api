CREATE TABLE outbox_event (
    secuencia BIGSERIAL PRIMARY KEY,
    event_id UUID NOT NULL UNIQUE,
    topico VARCHAR(160) NOT NULL,
    clave VARCHAR(100) NOT NULL,
    payload TEXT NOT NULL,
    publicado BOOLEAN NOT NULL DEFAULT FALSE,
    creado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    publicado_en TIMESTAMPTZ
);
CREATE INDEX outbox_pendientes ON outbox_event(secuencia) WHERE publicado=false;
CREATE TABLE inbox_event (
    consumidor VARCHAR(100) NOT NULL,
    event_id UUID NOT NULL,
    procesado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY(consumidor,event_id)
);
CREATE TABLE calificacion_saga (
    intento_id BIGINT PRIMARY KEY,
    sesion_id BIGINT NOT NULL,
    estudiante_id BIGINT NOT NULL,
    snapshot TEXT NOT NULL,
    estado VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE_RESULTADO',
    resultado_id BIGINT,
    actualizado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CHECK (estado IN ('PENDIENTE_RESULTADO','COMPLETADA'))
);
