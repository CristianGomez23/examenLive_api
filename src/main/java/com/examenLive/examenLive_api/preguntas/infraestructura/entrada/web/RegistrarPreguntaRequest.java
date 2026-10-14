package com.examenLive.examenLive_api.preguntas.infraestructura.entrada.web;

import java.math.BigDecimal;
import java.util.List;

public record RegistrarPreguntaRequest(
		String tipo,
		String enunciado,
		List<OpcionPreguntaRequest> opciones,
		BigDecimal puntaje
) {
}