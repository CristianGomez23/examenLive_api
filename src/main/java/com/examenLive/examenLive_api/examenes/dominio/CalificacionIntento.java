package com.examenLive.examenLive_api.examenes.dominio;

import java.math.BigDecimal;

public record CalificacionIntento(
		BigDecimal puntajeObtenido,
		BigDecimal puntajeMaximo,
		int preguntasCorrectas,
		int preguntasTotales
) {

	public CalificacionIntento {
		if (puntajeObtenido == null || puntajeObtenido.signum() < 0) {
			throw new IllegalArgumentException("El puntaje obtenido no puede ser negativo.");
		}
		if (puntajeMaximo == null || puntajeMaximo.signum() < 0) {
			throw new IllegalArgumentException("El puntaje máximo no puede ser negativo.");
		}
		if (preguntasCorrectas < 0 || preguntasTotales < 0 || preguntasCorrectas > preguntasTotales) {
			throw new IllegalArgumentException("El conteo de respuestas correctas no es válido.");
		}
	}
}