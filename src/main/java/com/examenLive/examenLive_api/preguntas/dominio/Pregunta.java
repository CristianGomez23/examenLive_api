package com.examenLive.examenLive_api.preguntas.dominio;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public final class Pregunta {

	private final UUID preguntaId;
	private final TipoPregunta tipo;
	private final String enunciado;
	private final Set<String> respuestasCorrectas;
	private final BigDecimal puntaje;
	private boolean activa;
	private boolean usadaEnSesionAplicada;

	public Pregunta(
			UUID preguntaId,
			TipoPregunta tipo,
			String enunciado,
			Set<String> respuestasCorrectas,
			BigDecimal puntaje
	) {
		if (preguntaId == null) {
			throw new IllegalArgumentException("El identificador de la pregunta es obligatorio.");
		}
		if (tipo == null) {
			throw new IllegalArgumentException("El tipo de pregunta es obligatorio.");
		}
		if (enunciado == null || enunciado.isBlank()) {
			throw new IllegalArgumentException("El enunciado de la pregunta es obligatorio.");
		}
		if (respuestasCorrectas == null || respuestasCorrectas.isEmpty()) {
			throw new IllegalArgumentException("La pregunta debe tener al menos una respuesta correcta.");
		}
		if (puntaje == null || puntaje.signum() <= 0) {
			throw new IllegalArgumentException("El puntaje de la pregunta debe ser positivo.");
		}

		LinkedHashSet<String> respuestasNormalizadas = new LinkedHashSet<>();
		for (String respuesta : respuestasCorrectas) {
			if (respuesta == null || respuesta.isBlank()) {
				throw new IllegalArgumentException("Las respuestas correctas no pueden ser nulas ni vacías.");
			}
			respuestasNormalizadas.add(respuesta.trim());
		}

		this.preguntaId = preguntaId;
		this.tipo = tipo;
		this.enunciado = enunciado.trim();
		this.respuestasCorrectas = Collections.unmodifiableSet(respuestasNormalizadas);
		this.puntaje = puntaje;
		this.activa = true;
	}

	public static Pregunta reconstruir(
			UUID preguntaId,
			TipoPregunta tipo,
			String enunciado,
			Set<String> respuestasCorrectas,
			BigDecimal puntaje,
			boolean activa,
			boolean usadaEnSesionAplicada
	) {
		Pregunta pregunta = new Pregunta(preguntaId, tipo, enunciado, respuestasCorrectas, puntaje);
		pregunta.activa = activa;
		pregunta.usadaEnSesionAplicada = usadaEnSesionAplicada;
		return pregunta;
	}

	public void registrarUsoEnSesionAplicada() {
		usadaEnSesionAplicada = true;
	}

	public void desactivar() {
		activa = false;
	}

	public boolean puedeEliminarse() {
		return !usadaEnSesionAplicada;
	}

	public UUID preguntaId() {
		return preguntaId;
	}

	public TipoPregunta tipo() {
		return tipo;
	}

	public String enunciado() {
		return enunciado;
	}

	public Set<String> respuestasCorrectas() {
		return respuestasCorrectas;
	}

	public BigDecimal puntaje() {
		return puntaje;
	}

	public boolean estaActiva() {
		return activa;
	}

	public boolean usadaEnSesionAplicada() {
		return usadaEnSesionAplicada;
	}
}