package com.examenLive.examenLive_api.preguntas.aplicacion;

import com.examenLive.examenLive_api.preguntas.dominio.Pregunta;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PreguntaRepository {

	void guardar(Pregunta pregunta);

	Optional<Pregunta> buscarPorId(UUID preguntaId);

	List<Pregunta> buscarActivas();

	void eliminar(Pregunta pregunta);
}