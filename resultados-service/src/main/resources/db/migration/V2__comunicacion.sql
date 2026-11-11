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
CREATE TABLE intento_resultado (
    intento_id BIGINT PRIMARY KEY,
    resultado_id BIGINT NOT NULL UNIQUE REFERENCES resultados(id),
    sesion_id BIGINT NOT NULL,
    estudiante_id BIGINT NOT NULL,
    UNIQUE(sesion_id,estudiante_id)
);
