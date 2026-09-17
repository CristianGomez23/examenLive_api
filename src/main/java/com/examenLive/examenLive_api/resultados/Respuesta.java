package com.examenLive.examenLive_api.resultados;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Value Object que representa la respuesta enviada para una pregunta.
 *
 * <p>El record y la copia defensiva de {@code valoresSeleccionados} garantizan
 * que una respuesta ya creada no pueda alterarse.</p>
 */
public record Respuesta(
		UUID preguntaId,
		Set<String> valoresSeleccionados,
		Instant enviadaEn
) {

	public Respuesta {
		if (preguntaId == null) {
			throw new IllegalArgumentException("La respuesta debe identificar la pregunta.");
		}
		if (enviadaEn == null) {
			throw new IllegalArgumentException("La respuesta debe registrar el instante de envío.");
		}
		if (valoresSeleccionados == null || valoresSeleccionados.isEmpty()) {
			throw new IllegalArgumentException("La respuesta debe contener al menos un valor seleccionado.");
		}

		LinkedHashSet<String> valoresNormalizados = new LinkedHashSet<>();
		for (String valor : valoresSeleccionados) {
			if (valor == null || valor.isBlank()) {
				throw new IllegalArgumentException("Los valores seleccionados no pueden ser nulos ni vacíos.");
			}
			valoresNormalizados.add(valor.trim());
		}
		valoresSeleccionados = Collections.unmodifiableSet(valoresNormalizados);
	}

	public boolean coincideCon(Set<String> respuestasCorrectas) {
		if (respuestasCorrectas == null) {
			return false;
		}

		LinkedHashSet<String> respuestasNormalizadas = new LinkedHashSet<>();
		for (String respuestaCorrecta : respuestasCorrectas) {
			if (respuestaCorrecta == null) {
				return false;
			}
			respuestasNormalizadas.add(respuestaCorrecta.trim());
		}
		return valoresSeleccionados.equals(respuestasNormalizadas);
	}
}
