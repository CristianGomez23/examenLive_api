package com.examenLive.examenesservice.examenes.dominio.puertos.entrada;

import java.util.List;

public record PreguntaEstudiante(
		Long preguntaId,
		String enunciado,
		List<String> opciones,
		List<String> seleccionadas,
		boolean respondida
) {
}
