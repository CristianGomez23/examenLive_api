CREATE SEQUENCE sesiones_id_seq;
CREATE TABLE sesiones(id BIGINT PRIMARY KEY,examen_id BIGINT NOT NULL,inicio TIMESTAMP NOT NULL,fin TIMESTAMP NOT NULL,CHECK(inicio<fin));
CREATE TABLE sesion_estudiantes(sesion_id BIGINT REFERENCES sesiones(id) ON DELETE CASCADE,estudiante_id BIGINT NOT NULL,PRIMARY KEY(sesion_id,estudiante_id));
