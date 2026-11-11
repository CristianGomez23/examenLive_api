package com.examenLive.examenLive_api.preguntas.aplicacion.servicios;

import com.examenLive.examenLive_api.identidad.dominio.puertos.salida.GeneradorId;
import com.examenLive.examenLive_api.preguntas.dominio.puertos.entrada.PreguntaUseCase;
import com.examenLive.examenLive_api.preguntas.dominio.puertos.salida.PreguntaRepository;
import com.examenLive.examenLive_api.preguntas.dominio.OpcionPregunta;
import com.examenLive.examenLive_api.preguntas.dominio.Pregunta;
import com.examenLive.examenLive_api.preguntas.dominio.TipoPregunta;
import com.examenLive.examenLive_api.identidad.dominio.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PreguntaService implements PreguntaUseCase {

	private final PreguntaRepository repositorio;
	private final GeneradorId generadorId;

	public PreguntaService(PreguntaRepository repositorio, GeneradorId generadorId) {
		this.repositorio = repositorio;
		this.generadorId = generadorId;
	}

	@Override
	public Pregunta registrar(
			TipoPregunta tipo,
			String enunciado,
			List<OpcionPregunta> opciones,
			BigDecimal puntaje
	) {
		if (opciones == null || opciones.stream().filter(opcion -> opcion != null && !opcion.correcta()).count() < 2) {
			throw new IllegalArgumentException("La pregunta debe tener al menos dos opciones incorrectas.");
		}
		Pregunta pregunta = new Pregunta(
				generadorId.siguienteId(), tipo, enunciado, opciones, puntaje
		);
		repositorio.guardar(pregunta);
		return pregunta;
	}

	@Override
	public List<Pregunta> consultarActivas() {
		return List.copyOf(repositorio.buscarActivas());
	}

	@Override
	public Pregunta consultar(Long preguntaId) {
		return buscarObligatoria(preguntaId);
	}

	@Override
	public void registrarUsoEnSesionAplicada(Long preguntaId) {
		Pregunta pregunta = buscarObligatoria(preguntaId);
		pregunta.registrarUsoEnSesionAplicada();
		repositorio.guardar(pregunta);
	}

	@Override
	public void desactivar(Long preguntaId) {
		Pregunta pregunta = buscarObligatoria(preguntaId);
		pregunta.desactivar();
		repositorio.guardar(pregunta);
	}

	@Override
	public void eliminar(Long preguntaId) {
		Pregunta pregunta = buscarObligatoria(preguntaId);
		if (!pregunta.puedeEliminarse()) {
			throw new IllegalStateException(
					"Una pregunta usada en una sesión aplicada solo puede desactivarse."
			);
		}
		repositorio.eliminar(pregunta);
	}

	private Pregunta buscarObligatoria(Long preguntaId) {
		if (preguntaId == null) {
			throw new IllegalArgumentException("El identificador de la pregunta es obligatorio.");
		}
		return repositorio.buscarPorId(preguntaId)
				.orElseThrow(() -> new RecursoNoEncontradoException("No existe la pregunta indicada."));
	}
}