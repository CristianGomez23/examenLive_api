package com.examenLive.examenLive_api.examenes.dominio.puertos.entrada;

import com.examenLive.examenLive_api.examenes.dominio.CalificacionIntento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record IntentoExamenVista(
		Long intentoId,
		Long sesionId,
		Long examenId,
		String titulo,
		LocalDateTime iniciadoEn,
		LocalDateTime venceEn,
		long segundosRestantes,
		List<PreguntaEstudiante> preguntas,
		int preguntasRespondidas,
		int preguntasPendientes,
		boolean finalizado,
		BigDecimal puntajeMaximo,
		CalificacionIntento calificacion
) {
}