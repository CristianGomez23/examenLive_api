package com.examenLive.examenLive_api.resultados.dominio;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Raíz del agregado que reúne las respuestas y la calificación de un estudiante.
 *
 * <p>Solo conserva los identificadores de sesión y estudiante. Las respuestas
 * y la calificación se modifican únicamente a través de esta raíz.</p>
 */
public final class Resultado {

	private final UUID resultadoId;
	private final UUID sesionId;
	private final UUID estudianteId;
	private final Instant creadoEn;
	private final Map<UUID, Respuesta> respuestasPorPregunta;
	private EstadoResultado estado;
	private Calificacion calificacion;

	public Resultado(UUID resultadoId, UUID sesionId, UUID estudianteId, Instant creadoEn) {
		this.resultadoId = resultadoId;
		this.sesionId = sesionId;
		this.estudianteId = estudianteId;
		this.creadoEn = creadoEn;
		this.respuestasPorPregunta = new LinkedHashMap<>();
		this.estado = EstadoResultado.EN_CURSO;
	}

	public void registrarRespuesta(Respuesta respuesta) {
		if (respuesta == null) {
			throw new IllegalArgumentException("La respuesta por registrar es obligatoria.");
		}
		if (estado != EstadoResultado.EN_CURSO) {
			throw new RespuestaNoModificableException(
					"No se pueden registrar respuestas después del cierre de la sesión."
			);
		}
		respuestasPorPregunta.put(respuesta.preguntaId(), respuesta);
	}

	public void cerrarRecepcionRespuestas() {
		if (estado != EstadoResultado.EN_CURSO) {
			throw new IllegalStateException("La recepción de respuestas ya fue cerrada.");
		}
		estado = EstadoResultado.CERRADO;
	}

	public void registrarCalificacion(Calificacion nuevaCalificacion) {
		if (nuevaCalificacion == null) {
			throw new IllegalArgumentException("La calificación es obligatoria.");
		}
		if (estado != EstadoResultado.CERRADO) {
			throw new IllegalStateException("Solo se puede calificar un resultado cerrado y aún no calificado.");
		}
		calificacion = nuevaCalificacion;
		estado = EstadoResultado.CALIFICADO;
	}

	public Optional<Respuesta> respuestaPara(UUID preguntaId) {
		return Optional.ofNullable(respuestasPorPregunta.get(preguntaId));
	}

	public List<Respuesta> respuestas() {
		return List.copyOf(new ArrayList<>(respuestasPorPregunta.values()));
	}

	public Optional<Calificacion> calificacion() {
		return Optional.ofNullable(calificacion);
	}

	public UUID resultadoId() {
		return resultadoId;
	}

	public UUID sesionId() {
		return sesionId;
	}

	public UUID estudianteId() {
		return estudianteId;
	}

	public Instant creadoEn() {
		return creadoEn;
	}

	public EstadoResultado estado() {
		return estado;
	}
}
