package com.examenLive.examenLive_api.preguntas.infraestructura.entrada.web;

import com.examenLive.examenLive_api.preguntas.dominio.Pregunta;

import java.math.BigDecimal;
import java.util.List;

public record PreguntaResponse(
		Long preguntaId,
		String tipo,
		String enunciado,
		List<OpcionPreguntaResponse> opciones,
		BigDecimal puntaje,
		boolean activa
) {

	public static PreguntaResponse desde(Pregunta pregunta) {
		return new PreguntaResponse(
				pregunta.preguntaId(),
				pregunta.tipo().nombre(),
				pregunta.enunciado(),
				pregunta.opciones().stream()
						.map(opcion -> new OpcionPreguntaResponse(opcion.texto(), opcion.correcta()))
						.toList(),
				pregunta.puntaje(),
				pregunta.estaActiva()
		);
	}
}