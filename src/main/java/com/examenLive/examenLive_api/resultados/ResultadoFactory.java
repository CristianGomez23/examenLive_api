package com.examenLive.examenLive_api.resultados;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

/**
 * Factory responsable de validar y construir la raíz del agregado Resultado.
 */
@Component
public class ResultadoFactory {

	public Resultado crear(UUID sesionId, UUID estudianteId) {
		validarReferencia(sesionId, "El resultado debe referenciar una sesión.");
		validarReferencia(estudianteId, "El resultado debe referenciar un estudiante.");
		return new Resultado(UUID.randomUUID(), sesionId, estudianteId, Instant.now());
	}

	private void validarReferencia(UUID referencia, String mensaje) {
		if (referencia == null) {
			throw new IllegalArgumentException(mensaje);
		}
	}
}
