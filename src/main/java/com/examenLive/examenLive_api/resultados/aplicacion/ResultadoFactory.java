package com.examenLive.examenLive_api.resultados.aplicacion;

import com.examenLive.examenLive_api.identidad.dominio.puertos.salida.GeneradorId;
import com.examenLive.examenLive_api.resultados.dominio.Resultado;

import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Factory responsable de validar y construir la raíz del agregado Resultado.
 */
@Component
public class ResultadoFactory {

	private final GeneradorId generadorId;

	public ResultadoFactory(GeneradorId generadorId) {
		this.generadorId = generadorId;
	}

	public Resultado crear(Long sesionId, Long estudianteId) {
		validarReferencia(sesionId, "El resultado debe referenciar una sesión.");
		validarReferencia(estudianteId, "El resultado debe referenciar un estudiante.");
		return new Resultado(generadorId.siguienteId(), sesionId, estudianteId, Instant.now());
	}

	private void validarReferencia(Long referencia, String mensaje) {
		if (referencia == null) {
			throw new IllegalArgumentException(mensaje);
		}
	}
}
