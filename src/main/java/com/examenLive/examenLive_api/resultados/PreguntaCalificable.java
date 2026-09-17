package com.examenLive.examenLive_api.resultados;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Proyección local de una pregunta con los datos mínimos para calificarla.
 *
 * <p>{@code puntajeAsignado} puede ser nulo porque este objeto también modela
 * la información incompleta que el servicio debe detectar antes de calificar.</p>
 */
public record PreguntaCalificable(
		UUID preguntaId,
		Set<String> respuestasCorrectas,
		BigDecimal puntajeAsignado
) {

	public PreguntaCalificable {
		if (preguntaId == null) {
			throw new IllegalArgumentException("La pregunta calificable debe tener un identificador.");
		}
		if (respuestasCorrectas == null || respuestasCorrectas.isEmpty()) {
			throw new IllegalArgumentException("La pregunta debe definir al menos una respuesta correcta.");
		}

		LinkedHashSet<String> respuestasNormalizadas = new LinkedHashSet<>();
		for (String respuestaCorrecta : respuestasCorrectas) {
			if (respuestaCorrecta == null || respuestaCorrecta.isBlank()) {
				throw new IllegalArgumentException("Las respuestas correctas no pueden ser nulas ni vacías.");
			}
			respuestasNormalizadas.add(respuestaCorrecta.trim());
		}
		respuestasCorrectas = Collections.unmodifiableSet(respuestasNormalizadas);

		if (puntajeAsignado != null && puntajeAsignado.signum() <= 0) {
			throw new IllegalArgumentException("El puntaje asignado debe ser positivo.");
		}
	}

	public boolean tienePuntajeAsignado() {
		return puntajeAsignado != null;
	}
}
