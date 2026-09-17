package com.examenLive.examenLive_api.resultados;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Servicio de dominio que califica un resultado usando criterios provenientes
 * del contexto de banco de preguntas y exámenes.
 */
@Service
public class CalificacionAutomaticaService {

	public Calificacion calificar(Resultado resultado, List<PreguntaCalificable> preguntas) {
		if (resultado == null) {
			throw new IllegalArgumentException("El resultado por calificar es obligatorio.");
		}
		if (resultado.estado() != EstadoResultado.CERRADO) {
			throw new IllegalStateException("El resultado debe estar cerrado antes de calificarse.");
		}
		if (preguntas == null || preguntas.isEmpty()) {
			throw new IllegalArgumentException("Se requiere al menos una pregunta para calificar.");
		}

		validarPreguntas(preguntas);

		Set<UUID> preguntasDelExamen = new HashSet<>();
		for (PreguntaCalificable pregunta : preguntas) {
			preguntasDelExamen.add(pregunta.preguntaId());
		}
		for (Respuesta respuesta : resultado.respuestas()) {
			if (!preguntasDelExamen.contains(respuesta.preguntaId())) {
				throw new IllegalArgumentException("El resultado contiene una respuesta ajena al examen.");
			}
		}

		BigDecimal puntajeObtenido = BigDecimal.ZERO;
		BigDecimal puntajeMaximo = BigDecimal.ZERO;
		int respuestasCorrectas = 0;

		for (PreguntaCalificable pregunta : preguntas) {
			puntajeMaximo = puntajeMaximo.add(pregunta.puntajeAsignado());
			boolean esCorrecta = resultado
					.respuestaPara(pregunta.preguntaId())
					.map(respuesta -> respuesta.coincideCon(pregunta.respuestasCorrectas()))
					.orElse(false);
			if (esCorrecta) {
				puntajeObtenido = puntajeObtenido.add(pregunta.puntajeAsignado());
				respuestasCorrectas++;
			}
		}

		Calificacion calificacion = new Calificacion(
				puntajeObtenido,
				puntajeMaximo,
				respuestasCorrectas,
				preguntas.size()
		);
		resultado.registrarCalificacion(calificacion);
		return calificacion;
	}

	private void validarPreguntas(List<PreguntaCalificable> preguntas) {
		Set<UUID> identificadores = new HashSet<>();
		for (PreguntaCalificable pregunta : preguntas) {
			if (pregunta == null) {
				throw new IllegalArgumentException("La lista de preguntas no puede contener valores nulos.");
			}
			if (!identificadores.add(pregunta.preguntaId())) {
				throw new IllegalArgumentException("No puede calificarse dos veces la misma pregunta.");
			}
			if (!pregunta.tienePuntajeAsignado()) {
				throw new PreguntaSinPuntajeException(
						"No se puede calificar el examen: hay preguntas sin puntaje asignado."
				);
			}
		}
	}
}
