package com.examenLive.examenLive_api.examenes.dominio;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;

public final class Examen {

	private final Long examenId;
	private final String titulo;
	private final List<Long> preguntasIds;
	private final Duration duracion;

	public Examen(Long examenId, String titulo, List<Long> preguntasIds, Duration duracion) {
		if (examenId == null || examenId <= 0) {
			throw new IllegalArgumentException("El identificador del examen debe ser positivo.");
		}
		if (titulo == null || titulo.isBlank()) {
			throw new IllegalArgumentException("El título del examen es obligatorio.");
		}
		if (preguntasIds == null || preguntasIds.isEmpty()) {
			throw new IllegalArgumentException("El examen debe contener preguntas.");
		}
		if (preguntasIds.stream().anyMatch(id -> id == null || id <= 0)) {
			throw new IllegalArgumentException("Los identificadores de pregunta deben ser positivos.");
		}
		if (new HashSet<>(preguntasIds).size() != preguntasIds.size()) {
			throw new IllegalArgumentException("El examen no puede repetir una pregunta.");
		}
		if (duracion == null || duracion.isZero() || duracion.isNegative()) {
			throw new IllegalArgumentException("La duración del examen debe ser positiva.");
		}

		this.examenId = examenId;
		this.titulo = titulo.trim();
		this.preguntasIds = List.copyOf(preguntasIds);
		this.duracion = duracion;
	}

	public Long examenId() {
		return examenId;
	}

	public String titulo() {
		return titulo;
	}

	public List<Long> preguntasIds() {
		return preguntasIds;
	}

	public Duration duracion() {
		return duracion;
	}
}