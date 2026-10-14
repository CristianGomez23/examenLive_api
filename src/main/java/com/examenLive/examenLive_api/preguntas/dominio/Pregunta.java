package com.examenLive.examenLive_api.preguntas.dominio;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class Pregunta {

	private final Long preguntaId;
	private final TipoPregunta tipo;
	private final String enunciado;
	private final List<OpcionPregunta> opciones;
	private final BigDecimal puntaje;
	private boolean activa;
	private boolean usadaEnSesionAplicada;

	public Pregunta(
			Long preguntaId,
			TipoPregunta tipo,
			String enunciado,
			List<OpcionPregunta> opciones,
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
		if (opciones == null || opciones.isEmpty()) {
			throw new IllegalArgumentException("La pregunta debe tener opciones.");
		}
		if (puntaje == null || puntaje.signum() <= 0) {
			throw new IllegalArgumentException("El puntaje de la pregunta debe ser positivo.");
		}

		ArrayList<OpcionPregunta> opcionesValidadas = new ArrayList<>();
		LinkedHashSet<String> textos = new LinkedHashSet<>();
		boolean tieneCorrecta = false;
		for (OpcionPregunta opcion : opciones) {
			if (opcion == null) {
				throw new IllegalArgumentException("Las opciones no pueden ser nulas.");
			}
			if (!textos.add(opcion.texto())) {
				throw new IllegalArgumentException("No se permiten opciones repetidas.");
			}
			opcionesValidadas.add(opcion);
			tieneCorrecta |= opcion.correcta();
		}
		if (!tieneCorrecta) {
			throw new IllegalArgumentException("La pregunta debe tener al menos una opción correcta.");
		}

		this.preguntaId = preguntaId;
		this.tipo = tipo;
		this.enunciado = enunciado.trim();
		this.opciones = List.copyOf(opcionesValidadas);
		this.puntaje = puntaje;
		this.activa = true;
	}

	public static Pregunta reconstruir(
			Long preguntaId,
			TipoPregunta tipo,
			String enunciado,
			List<OpcionPregunta> opciones,
			BigDecimal puntaje,
			boolean activa,
			boolean usadaEnSesionAplicada
	) {
		Pregunta pregunta = new Pregunta(preguntaId, tipo, enunciado, opciones, puntaje);
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

	public Long preguntaId() {
		return preguntaId;
	}

	public TipoPregunta tipo() {
		return tipo;
	}

	public String enunciado() {
		return enunciado;
	}

	public Set<String> respuestasCorrectas() {
		return opciones.stream()
				.filter(OpcionPregunta::correcta)
				.map(OpcionPregunta::texto)
				.collect(java.util.stream.Collectors.toUnmodifiableSet());
	}

	public List<OpcionPregunta> opciones() {
		return opciones;
	}

	public boolean tieneOpcionesIncorrectasSuficientes() {
		return opciones.stream().filter(opcion -> !opcion.correcta()).count() >= 2;
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