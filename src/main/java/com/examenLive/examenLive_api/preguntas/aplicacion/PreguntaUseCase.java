package com.examenLive.examenLive_api.preguntas.aplicacion;

import com.examenLive.examenLive_api.preguntas.dominio.Pregunta;
import com.examenLive.examenLive_api.preguntas.dominio.TipoPregunta;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface PreguntaUseCase {

	Pregunta registrar(TipoPregunta tipo, String enunciado, Set<String> respuestasCorrectas, BigDecimal puntaje);

	List<Pregunta> consultarActivas();

	Pregunta consultar(UUID preguntaId);

	void registrarUsoEnSesionAplicada(UUID preguntaId);

	void desactivar(UUID preguntaId);

	void eliminar(UUID preguntaId);
}