ALTER TABLE examenes
    ADD COLUMN IF NOT EXISTS duracion_minutos BIGINT NOT NULL DEFAULT 60;

CREATE TABLE IF NOT EXISTS sesiones (
    id BIGINT PRIMARY KEY,
    examen_id BIGINT NOT NULL REFERENCES examenes(id),
    inicio TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    fin TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT sesiones_ventana_valida CHECK (inicio < fin)
);

CREATE TABLE IF NOT EXISTS sesion_estudiantes (
    sesion_id BIGINT NOT NULL REFERENCES sesiones(id) ON DELETE CASCADE,
    estudiante_id BIGINT NOT NULL,
    PRIMARY KEY (sesion_id, estudiante_id)
);

ALTER TABLE intentos_examen
    ADD COLUMN IF NOT EXISTS sesion_id BIGINT REFERENCES sesiones(id),
    ADD COLUMN IF NOT EXISTS iniciado_en TIMESTAMP WITHOUT TIME ZONE,
    ADD COLUMN IF NOT EXISTS vence_en TIMESTAMP WITHOUT TIME ZONE;

CREATE UNIQUE INDEX IF NOT EXISTS intentos_sesion_estudiante_unicos
    ON intentos_examen (sesion_id, estudiante_id)
    WHERE sesion_id IS NOT NULL;

CREATE INDEX IF NOT EXISTS intentos_vencimiento_pendiente
    ON intentos_examen (vence_en)
    WHERE puntaje_obtenido IS NULL;