package com.examenLive.examenLive_api.preguntas;

import com.examenLive.examenLive_api.identidad.GeneradorIdNumericoFake;
import com.examenLive.examenLive_api.preguntas.dominio.puertos.salida.PreguntaRepository;
import com.examenLive.examenLive_api.preguntas.aplicacion.servicios.PreguntaService;
import com.examenLive.examenLive_api.preguntas.dominio.OpcionPregunta;
import com.examenLive.examenLive_api.preguntas.dominio.Pregunta;
import com.examenLive.examenLive_api.preguntas.dominio.TipoPregunta;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PreguntaServiceTest {

	@Test
	void registraYConsultaPreguntaActiva() {
		FakePreguntaRepository repositorio = new FakePreguntaRepository();
		PreguntaService servicio = new PreguntaService(
				repositorio, new GeneradorIdNumericoFake(1)
		);

		Pregunta pregunta = servicio.registrar(
				new TipoPregunta("selección múltiple"),
				"¿Cuánto es 2 + 2?",
				opciones("4", "3", "5"),
				new BigDecimal("2.5")
		);

		assertEquals(pregunta, servicio.consultarActivas().getFirst());
		assertEquals(new BigDecimal("2.5"), pregunta.puntaje());
	}

	@Test
	void noPermiteEliminarPreguntaUsadaEnSesionAplicada() {
		FakePreguntaRepository repositorio = new FakePreguntaRepository();
		PreguntaService servicio = new PreguntaService(
				repositorio, new GeneradorIdNumericoFake(1)
		);
		Pregunta pregunta = servicio.registrar(
				new TipoPregunta("selección múltiple"), "Enunciado", opciones("A", "B", "C"), BigDecimal.ONE
		);
		servicio.registrarUsoEnSesionAplicada(pregunta.preguntaId());

		assertThrows(IllegalStateException.class, () -> servicio.eliminar(pregunta.preguntaId()));
		servicio.desactivar(pregunta.preguntaId());

		assertFalse(pregunta.estaActiva());
		assertTrue(repositorio.preguntas.containsKey(pregunta.preguntaId()));
	}

	@Test
	void exigePuntajePositivoAlRegistrarPregunta() {
		PreguntaService servicio = new PreguntaService(
				new FakePreguntaRepository(), new GeneradorIdNumericoFake(1)
		);

		assertThrows(IllegalArgumentException.class, () -> servicio.registrar(
				new TipoPregunta("selección múltiple"), "Enunciado", opciones("A", "B", "C"), BigDecimal.ZERO
		));
	}

	private static List<OpcionPregunta> opciones(String correcta, String incorrectaUno, String incorrectaDos) {
		List<OpcionPregunta> opciones = new ArrayList<>();
		opciones.add(new OpcionPregunta(correcta, true));
		opciones.add(new OpcionPregunta(incorrectaUno, false));
		opciones.add(new OpcionPregunta(incorrectaDos, false));
		return opciones;
	}

	private static class FakePreguntaRepository implements PreguntaRepository {

		private final Map<Long, Pregunta> preguntas = new HashMap<>();

		@Override
		public void guardar(Pregunta pregunta) {
			preguntas.put(pregunta.preguntaId(), pregunta);
		}

		@Override
		public Optional<Pregunta> buscarPorId(Long preguntaId) {
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