package com.examenLive.preguntasservice.preguntas.dominio;

public record TipoPregunta(String nombre) {

	public TipoPregunta {
		if (nombre == null || nombre.isBlank()) {
			throw new IllegalArgumentException("El tipo de pregunta es obligatorio.");
		}
		nombre = nombre.trim();
	}
}
