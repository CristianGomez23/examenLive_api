package com.examenLive.examenLive_api.preguntas;

import com.examenLive.examenLive_api.preguntas.aplicacion.BancoPreguntasService;
import com.examenLive.examenLive_api.preguntas.aplicacion.PreguntaRepository;
import com.examenLive.examenLive_api.preguntas.dominio.Pregunta;
import com.examenLive.examenLive_api.preguntas.dominio.TipoPregunta;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BancoPreguntasServiceTest {

	@Test
	void registraYConsultaPreguntaActiva() {
		FakePreguntaRepository repositorio = new FakePreguntaRepository();
		BancoPreguntasService servicio = new BancoPreguntasService(repositorio);

		Pregunta pregunta = servicio.registrar(
				new TipoPregunta("opción única"), "¿Cuánto es 2 + 2?", Set.of("4"), new BigDecimal("2.5")
		);

		assertEquals(pregunta, servicio.consultarActivas().getFirst());
		assertEquals(new BigDecimal("2.5"), pregunta.puntaje());
	}

	@Test
	void noPermiteEliminarPreguntaUsadaEnSesionAplicada() {
		FakePreguntaRepository repositorio = new FakePreguntaRepository();
		BancoPreguntasService servicio = new BancoPreguntasService(repositorio);
		Pregunta pregunta = servicio.registrar(
				new TipoPregunta("opción única"), "Enunciado", Set.of("A"), BigDecimal.ONE
		);
		servicio.registrarUsoEnSesionAplicada(pregunta.preguntaId());

		assertThrows(IllegalStateException.class, () -> servicio.eliminar(pregunta.preguntaId()));
		servicio.desactivar(pregunta.preguntaId());

		assertFalse(pregunta.estaActiva());
		assertTrue(repositorio.preguntas.containsKey(pregunta.preguntaId()));
	}

	@Test
	void exigePuntajePositivoAlRegistrarPregunta() {
		BancoPreguntasService servicio = new BancoPreguntasService(new FakePreguntaRepository());

		assertThrows(IllegalArgumentException.class, () -> servicio.registrar(
				new TipoPregunta("opción única"), "Enunciado", Set.of("A"), BigDecimal.ZERO
		));
	}

	private static class FakePreguntaRepository implements PreguntaRepository {

		private final Map<UUID, Pregunta> preguntas = new HashMap<>();

		@Override
		public void guardar(Pregunta pregunta) {
			preguntas.put(pregunta.preguntaId(), pregunta);
		}

		@Override
		public Optional<Pregunta> buscarPorId(UUID preguntaId) {
			return Optional.ofNullable(preguntas.get(preguntaId));
		}

		@Override
		public List<Pregunta> buscarActivas() {
			return preguntas.values().stream().filter(Pregunta::estaActiva).toList();
		}

		@Override
		public void eliminar(Pregunta pregunta) {
			preguntas.remove(pregunta.preguntaId());
		}
	}
}