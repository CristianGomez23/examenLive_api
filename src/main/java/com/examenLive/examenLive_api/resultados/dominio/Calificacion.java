package com.examenLive.examenLive_api.resultados.dominio;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Valor calculado por el servicio de calificación automática.
 */
public record Calificacion(
		BigDecimal puntajeObtenido,
		BigDecimal puntajeMaximo,
		int respuestasCorrectas,
		int totalPreguntas
) {

	public Calificacion {
		if (puntajeObtenido == null || puntajeMaximo == null) {
			throw new IllegalArgumentException("La calificación debe incluir los puntajes obtenido y máximo.");
		}
		if (puntajeMaximo.signum() <= 0) {
			throw new IllegalArgumentException("El puntaje máximo debe ser positivo.");
		}
		if (puntajeObtenido.signum() < 0 || puntajeObtenido.compareTo(puntajeMaximo) > 0) {
			throw new IllegalArgumentException("El puntaje obtenido debe estar entre cero y el puntaje máximo.");
		}
		if (totalPreguntas <= 0 || respuestasCorrectas < 0 || respuestasCorrectas > totalPreguntas) {
			throw new IllegalArgumentException("Las cantidades de preguntas de la calificación no son válidas.");
		}
	}

	public BigDecimal porcentaje() {
		return puntajeObtenido
				.multiply(BigDecimal.valueOf(100))
				.divide(puntajeMaximo, 2, RoundingMode.HALF_UP);
	}
}
