package com.examenLive.resultadosservice.resultados.infraestructura.entrada.web;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * Contrato con quien finaliza un intento (hoy el servicio de exámenes). Es autocontenido:
 * trae las respuestas del estudiante y los datos mínimos de calificación de cada pregunta,
 * de modo que este servicio NO necesita llamar a otro servicio para calificar
 * (patrón Self-contained Service).
 */
public record GenerarResultadoRequest(
		Long sesionId,
		Long estudianteId,
		List<RespuestaRequest> respuestas,
		List<PreguntaRequest> preguntas
) {

	public record RespuestaRequest(Long preguntaId, Set<String> opcionesSeleccionadas) {
	}

	public record PreguntaRequest(Long preguntaId, Set<String> respuestasCorrectas, BigDecimal puntaje) {
	}
}
