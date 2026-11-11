-- Esquema PRIVADO del microservicio de resultados (Database per Service).
-- Flyway es el único dueño del esquema; Hibernate solo lo valida (ddl-auto: validate).

-- Ids de resultados: secuencia propia del servicio (ya no existe la secuencia global del monolito).
CREATE SEQUENCE IF NOT EXISTS resultados_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE resultados (
    id                   BIGINT        NOT NULL PRIMARY KEY,
    sesion_id            BIGINT        NOT NULL,
    estudiante_id        BIGINT        NOT NULL,
    creado_en            TIMESTAMP WITH TIME ZONE NOT NULL,
    estado               VARCHAR(255)  NOT NULL,
    puntaje_obtenido     NUMERIC(10, 2),
    puntaje_maximo       NUMERIC(10, 2),
    respuestas_correctas INTEGER,
    total_preguntas      INTEGER,
    -- Un solo resultado por estudiante y sesión: respalda la idempotencia de POST /api/resultados.
    CONSTRAINT resultados_sesion_estudiante_unico UNIQUE (sesion_id, estudiante_id)
);

CREATE INDEX resultados_estudiante_idx ON resultados (estudiante_id, creado_en DESC);

-- Una fila por cada opción seleccionada en una pregunta.
CREATE TABLE resultado_respuestas (
    resultado_id BIGINT        NOT NULL REFERENCES resultados (id) ON DELETE CASCADE,
    pregunta_id  BIGINT        NOT NULL,
    valor        VARCHAR(255)  NOT NULL,
    enviada_en   TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX resultado_respuestas_resultado_idx ON resultado_respuestas (resultado_id);
