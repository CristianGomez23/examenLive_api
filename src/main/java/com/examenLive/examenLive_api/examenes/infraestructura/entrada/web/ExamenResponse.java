package com.examenLive.examenLive_api.examenes.infraestructura.entrada.web;

import com.examenLive.examenLive_api.examenes.dominio.Examen;

import java.util.List;

public record ExamenResponse(Long examenId, String titulo, List<Long> preguntasIds, long duracionMinutos) {

	public static ExamenResponse desde(Examen examen) {
		return new ExamenResponse(
				examen.examenId(), examen.titulo(), examen.preguntasIds(), examen.duracion().toMinutes()
		);
	}
}