package com.examenLive.examenLive_api.examenes.dominio;

import com.examenLive.examenLive_api.preguntas.dominio.Pregunta;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class IntentoExamen {

	private final Long intentoId;
	private final Long sesionId;
	private final Long examenId;
	private final Long estudianteId;
	private final List<Long> preguntasIds;
	private final LocalDateTime iniciadoEn;
	private final LocalDateTime venceEn;
	private final Map<Long, Set<String>> respuestasPorPregunta;
	private CalificacionIntento calificacion;

	public IntentoExamen(
			Long intentoId,
			Long sesionId,
			Long examenId,
			Long estudianteId,
			List<Long> preguntasIds,
			LocalDateTime iniciadoEn,
			LocalDateTime venceEn
	) {
		this(intentoId, sesionId, examenId, estudianteId, preguntasIds, iniciadoEn, venceEn, Map.of(), null);
	}

	private IntentoExamen(
			Long intentoId,
			Long sesionId,
			Long examenId,
			Long estudianteId,
			List<Long> preguntasIds,
			LocalDateTime iniciadoEn,
			LocalDateTime venceEn,
			Map<Long, Set<String>> respuestas,
			CalificacionIntento calificacion
	) {
		if (intentoId == null || intentoId <= 0 || sesionId == null || sesionId <= 0
				|| examenId == null || examenId <= 0
				|| estudianteId == null || estudianteId <= 0) {
			throw new IllegalArgumentException("Los identificadores del intento deben ser positivos.");
		}
		if (iniciadoEn == null || venceEn == null || !iniciadoEn.isBefore(venceEn)) {
			throw new IllegalArgumentException("El intento debe tener un inicio y un vencimiento posteriores al inicio.");
		}
		if (preguntasIds == null || preguntasIds.isEmpty()) {
			throw new IllegalArgumentException("El intento debe tener preguntas.");
		}
		this.intentoId = intentoId;
		this.sesionId = sesionId;
		this.examenId = examenId;
		this.estudianteId = estudianteId;
		this.preguntasIds = List.copyOf(preguntasIds);
		this.iniciadoEn = iniciadoEn;
		this.venceEn = venceEn;
		this.respuestasPorPregunta = new HashMap<>();
		respuestas.forEach((id, seleccionadas) ->
				this.respuestasPorPregunta.put(id, Set.copyOf(seleccionadas))
		);
		this.calificacion = calificacion;
	}

	public static IntentoExamen reconstruir(
			Long intentoId,
			Long sesionId,
			Long examenId,
			Long estudianteId,
			List<Long> preguntasIds,
			LocalDateTime iniciadoEn,
			LocalDateTime venceEn,
			Map<Long, Set<String>> respuestas,
			CalificacionIntento calificacion
	) {
		return new IntentoExamen(
				intentoId, sesionId, examenId, estudianteId, preguntasIds, iniciadoEn, venceEn, respuestas, calificacion
		);
	}

	public boolean estaVencido(LocalDateTime ahora) {
		return !ahora.isBefore(venceEn);
	}

	public void responder(Long preguntaId, Set<String> seleccionadas, Pregunta pregunta) {
		if (finalizado()) {
			throw new IllegalStateException("El intento ya fue finalizado.");
		}
		if (!preguntasIds.contains(preguntaId) || !pregunta.preguntaId().equals(preguntaId)) {
			throw new IllegalArgumentException("La pregunta no pertenece a este examen.");
		}
		if (seleccionadas == null || seleccionadas.isEmpty()) {
			throw new IllegalArgumentException("Selecciona al menos una opción para responder.");
		}
		Set<String> opcionesDisponibles = new HashSet<>();
		pregunta.opciones().forEach(opcion -> opcionesDisponibles.add(opcion.texto()));
		LinkedHashSet<String> seleccionadasNormalizadas = new LinkedHashSet<>();
		for (String opcion : seleccionadas) {
			if (opcion == null || !opcionesDisponibles.contains(opcion.trim())) {
				throw new IllegalArgumentException("La respuesta incluye una opción que no pertenece a la pregunta.");
			}
			seleccionadasNormalizadas.add(opcion.trim());
		}
		respuestasPorPregunta.put(preguntaId, Set.copyOf(seleccionadasNormalizadas));
	}

	public CalificacionIntento finalizar(List<Pregunta> preguntas) {
		if (finalizado()) {
			throw new IllegalStateException("El intento ya fue finalizado.");
		}
		if (preguntas == null || preguntas.size() != preguntasIds.size()) {
			throw new IllegalArgumentException("Las preguntas del intento no están completas.");
		}

		BigDecimal puntajeObtenido = BigDecimal.ZERO;
		BigDecimal puntajeMaximo = BigDecimal.ZERO;
		int preguntasCorrectas = 0;
		for (Pregunta pregunta : preguntas) {
			if (!preguntasIds.contains(pregunta.preguntaId())) {
				throw new IllegalArgumentException("Se intentó calificar una pregunta ajena al examen.");
			}
			puntajeMaximo = puntajeMaximo.add(pregunta.puntaje());
			Set<String> seleccionadas = respuestasPorPregunta.get(pregunta.preguntaId());
			if (seleccionadas != null && seleccionadas.equals(pregunta.respuestasCorrectas())) {
				puntajeObtenido = puntajeObtenido.add(pregunta.puntaje());
				preguntasCorrectas++;
			}
		}
		calificacion = new CalificacionIntento(
				puntajeObtenido, puntajeMaximo, preguntasCorrectas, preguntasIds.size()
		);
		return calificacion;
	}

	public Long intentoId() {
		return intentoId;
	}

	public Long examenId() {
		return examenId;
	}

	public Long sesionId() {
		return sesionId;
	}

	public Long estudianteId() {
		return estudianteId;
	}

	public LocalDateTime iniciadoEn() {
		return iniciadoEn;
	}

	public LocalDateTime venceEn() {
		return venceEn;
	}

	public List<Long> preguntasIds() {
		return preguntasIds;
	}

	public Map<Long, Set<String>> respuestasPorPregunta() {
		return Map.copyOf(respuestasPorPregunta);
	}

	public int preguntasRespondidas() {
		return respuestasPorPregunta.size();
	}

	public int preguntasPendientes() {
		return preguntasIds.size() - preguntasRespondidas();
	}

	public boolean finalizado() {
		return calificacion != null;
	}

	public CalificacionIntento calificacion() {
		return calificacion;
	}
}