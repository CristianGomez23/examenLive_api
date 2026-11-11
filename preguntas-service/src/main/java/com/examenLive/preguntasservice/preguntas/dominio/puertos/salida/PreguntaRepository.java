package com.examenLive.preguntasservice.preguntas.dominio.puertos.salida;

import com.examenLive.preguntasservice.preguntas.dominio.Pregunta;

import java.util.List;
import java.util.Optional;

public interface PreguntaRepository {

	void guardar(Pregunta pregunta);

	Optional<Pregunta> buscarPorId(Long preguntaId);

	List<Pregunta> buscarActivas();

	void eliminar(Pregunta pregunta);
}
