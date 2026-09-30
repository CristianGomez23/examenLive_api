package com.examenLive.examenLive_api.preguntas.aplicacion;

import com.examenLive.examenLive_api.preguntas.dominio.Pregunta;
import com.examenLive.examenLive_api.preguntas.dominio.TipoPregunta;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class BancoPreguntasService implements PreguntaUseCase {

	private final PreguntaRepository repositorio;

	public BancoPreguntasService(PreguntaRepository repositorio) {
		this.repositorio = repositorio;
	}

	@Override
	public Pregunta registrar(
			TipoPregunta tipo,
			String enunciado,
			Set<String> respuestasCorrectas,
			BigDecimal puntaje
	) {
		Pregunta pregunta = new Pregunta(
				UUID.randomUUID(), tipo, enunciado, respuestasCorrectas, puntaje
		);
		repositorio.guardar(pregunta);
		return pregunta;
	}

	@Override
	public List<Pregunta> consultarActivas() {
		return List.copyOf(repositorio.buscarActivas());
	}

	@Override
	public Pregunta consultar(UUID preguntaId) {
		return buscarObligatoria(preguntaId);
	}

	@Override
	public void registrarUsoEnSesionAplicada(UUID preguntaId) {
		Pregunta pregunta = buscarObligatoria(preguntaId);
		pregunta.registrarUsoEnSesionAplicada();
		repositorio.guardar(pregunta);
	}

	@Override
	public void desactivar(UUID preguntaId) {
		Pregunta pregunta = buscarObligatoria(preguntaId);
		pregunta.desactivar();
		repositorio.guardar(pregunta);
	}

	@Override
	public void eliminar(UUID preguntaId) {
		Pregunta pregunta = buscarObligatoria(preguntaId);
		if (!pregunta.puedeEliminarse()) {
			throw new IllegalStateException(
					"Una pregunta usada en una sesión aplicada solo puede desactivarse."
			);
		}
		repositorio.eliminar(pregunta);
	}

	private Pregunta buscarObligatoria(UUID preguntaId) {
		if (preguntaId == null) {
			throw new IllegalArgumentException("El identificador de la pregunta es obligatorio.");
		}
		return repositorio.buscarPorId(preguntaId)
				.orElseThrow(() -> new IllegalArgumentException("No existe la pregunta indicada."));
	}
}