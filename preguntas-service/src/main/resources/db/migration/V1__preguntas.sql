CREATE SEQUENCE preguntas_id_seq;
CREATE TABLE preguntas(id BIGINT PRIMARY KEY,tipo VARCHAR(255),enunciado VARCHAR(2000) NOT NULL,puntaje NUMERIC(10,2) NOT NULL,activa BOOLEAN NOT NULL,usada_en_sesion_aplicada BOOLEAN NOT NULL);
CREATE TABLE pregunta_opciones(pregunta_id BIGINT REFERENCES preguntas(id) ON DELETE CASCADE,orden INTEGER NOT NULL,texto VARCHAR(255) NOT NULL,correcta BOOLEAN NOT NULL,PRIMARY KEY(pregunta_id,orden));
