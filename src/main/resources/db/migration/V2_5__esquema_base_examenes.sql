-- La migración V3 modifica/referencia "examenes" e "intentos_examen", pero esas tablas
-- las creaba Hibernate (ddl-auto: update) DESPUÉS de que Flyway ya había corrido.
-- En una base de datos nueva (o con el contenedor recién creado) V3 fallaba con:
--   ERROR: relation "examenes" does not exist
-- Esta migración (versión 2.5, se ejecuta antes que V3) crea el esquema base de forma
-- idempotente. Las columnas nuevas de V3 se siguen agregando allí; el resto de tablas
-- auxiliares (examen_preguntas, intento_preguntas, intento_respuestas, resultados...)
-- las completa Hibernate.

CREATE TABLE IF NOT EXISTS examenes (
    id BIGINT PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL
);

CREATE TABLE IF NOT EXISTS intentos_examen (
    id BIGINT PRIMARY KEY,
    examen_id BIGINT NOT NULL,
    estudiante_id BIGINT NOT NULL,
    puntaje_obtenido NUMERIC(38, 2),
    puntaje_maximo NUMERIC(38, 2),
    preguntas_correctas INTEGER,
    preguntas_totales INTEGER
);
