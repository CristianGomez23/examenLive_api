package com.examenLive.examenLive_api.preguntas.infraestructura.entrada.web;

import com.examenLive.examenLive_api.preguntas.dominio.Pregunta;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public record PreguntaResponse(
		UUID preguntaId,
		String tipo,
		String enunciado,
		Set<String> respuestasCorrectas,
		BigDecimal puntaje,
		boolean activa
) {

	public static PreguntaResponse desde(Pregunta pregunta) {
		return new PreguntaResponse(
				pregunta.preguntaId(),
				pregunta.tipo().nombre(),
				pregunta.enunciado(),
				pregunta.respuestasCorrectas(),
				pregunta.puntaje(),
				pregunta.estaActiva()
		);
	}
}