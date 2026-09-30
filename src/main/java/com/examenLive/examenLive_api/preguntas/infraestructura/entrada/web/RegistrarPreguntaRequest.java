package com.examenLive.examenLive_api.preguntas.infraestructura.entrada.web;

import java.math.BigDecimal;
import java.util.Set;

public record RegistrarPreguntaRequest(
		String tipo,
		String enunciado,
		Set<String> respuestasCorrectas,
		BigDecimal puntaje
) {
}