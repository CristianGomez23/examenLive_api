package com.examenLive.examenLive_api.preguntas.dominio;

public record OpcionPregunta(String texto, boolean correcta) {

	public OpcionPregunta {
		if (texto == null || texto.isBlank()) {
			throw new IllegalArgumentException("El texto de la opción es obligatorio.");
		}
		texto = texto.trim();
	}
}