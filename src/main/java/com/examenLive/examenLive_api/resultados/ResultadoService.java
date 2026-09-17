package com.examenLive.examenLive_api.resultados;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Orquesta los casos de uso del bounded context y delega las reglas al dominio.
 */
@Service
public class ResultadoService {

	private final ResultadoFactory resultadoFactory;
	private final CalificacionAutomaticaService calificacionAutomaticaService;

	public ResultadoService(
			ResultadoFactory resultadoFactory,
			CalificacionAutomaticaService calificacionAutomaticaService
	) {
		this.resultadoFactory = resultadoFactory;
		this.calificacionAutomaticaService = calificacionAutomaticaService;
	}

	public Resultado iniciar(UUID sesionId, UUID estudianteId) {
		return resultadoFactory.crear(sesionId, estudianteId);
	}

	public void registrarRespuesta(Resultado resultado, Respuesta respuesta) {
		resultado.registrarRespuesta(respuesta);
	}

	public Calificacion cerrarYCalificar(
			Resultado resultado,
			List<PreguntaCalificable> preguntas
	) {
		resultado.cerrarRecepcionRespuestas();
		return calificacionAutomaticaService.calificar(resultado, preguntas);
	}
}
