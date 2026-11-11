CREATE SEQUENCE examenes_id_seq;
CREATE TABLE examenes(id BIGINT PRIMARY KEY,titulo VARCHAR(200) NOT NULL,duracion_minutos BIGINT NOT NULL);
CREATE TABLE examen_preguntas(examen_id BIGINT REFERENCES examenes(id) ON DELETE CASCADE,orden INTEGER NOT NULL,pregunta_id BIGINT NOT NULL,PRIMARY KEY(examen_id,orden));
CREATE TABLE intentos_examen(id BIGINT PRIMARY KEY,examen_id BIGINT NOT NULL REFERENCES examenes(id),sesion_id BIGINT NOT NULL,estudiante_id BIGINT NOT NULL,iniciado_en TIMESTAMP,vence_en TIMESTAMP,puntaje_obtenido NUMERIC(38,2),puntaje_maximo NUMERIC(38,2),preguntas_correctas INTEGER,preguntas_totales INTEGER,UNIQUE(sesion_id,estudiante_id));
CREATE TABLE intento_preguntas(intento_id BIGINT REFERENCES intentos_examen(id) ON DELETE CASCADE,orden INTEGER NOT NULL,pregunta_id BIGINT NOT NULL,PRIMARY KEY(intento_id,orden));
CREATE TABLE intento_respuestas(intento_id BIGINT REFERENCES intentos_examen(id) ON DELETE CASCADE,orden INTEGER NOT NULL,pregunta_id BIGINT NOT NULL,opcion VARCHAR(255) NOT NULL,PRIMARY KEY(intento_id,orden));
CREATE INDEX intentos_vencidos ON intentos_examen(vence_en) WHERE puntaje_obtenido IS NULL;
