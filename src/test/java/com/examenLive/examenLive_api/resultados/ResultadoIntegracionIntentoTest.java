package com.examenLive.examenLive_api.resultados;

import com.examenLive.examenLive_api.identidad.GeneradorIdNumericoFake;
import com.examenLive.examenLive_api.identidad.dominio.RecursoNoEncontradoException;
import com.examenLive.examenLive_api.resultados.aplicacion.ResultadoFactory;
import com.examenLive.examenLive_api.resultados.aplicacion.servicios.CalificacionAutomaticaService;
import com.examenLive.examenLive_api.resultados.aplicacion.servicios.ResultadoService;
import com.examenLive.examenLive_api.resultados.dominio.EstadoResultado;
import com.examenLive.examenLive_api.resultados.dominio.PreguntaCalificable;
import com.examenLive.examenLive_api.resultados.dominio.Resultado;
import com.examenLive.examenLive_api.resultados.dominio.Respuesta;
import com.examenLive.examenLive_api.resultados.dominio.puertos.salida.RepositorioResultados;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cubre el bug reportado: al terminar un intento no se generaba resultado (ni id) consultable.
 */
class ResultadoIntegracionIntentoTest {

	private final Map<Long, Resultado> datos = new HashMap<>();
	private final RepositorioResultados repositorio = new RepositorioResultados() {
		public Resultado guardar(Resultado r) { datos.put(r.resultadoId(), r); return r; }
		public Optional<Resultado> buscarPorId(Long id) { return Optional.ofNullable(datos.get(id)); }
		public Optional<Resultado> buscarPorSesionYEstudiante(Long s, Long e) {
			return datos.values().stream().filter(r -> r.sesionId().equals(s) && r.estudianteId().equals(e)).findFirst();
		}
		public List<Resultado> buscarPorEstudiante(Long e) {
			return datos.values().stream().filter(r -> r.estudianteId().equals(e)).toList();
		}
	};
	private final ResultadoService servicio = new ResultadoService(
			repositorio, new ResultadoFactory(new GeneradorIdNumericoFake(500)), new CalificacionAutomaticaService()
	);

	private static List<PreguntaCalificable> preguntas() {
		return List.of(
				new PreguntaCalificable(1L, Set.of("A", "B"), new BigDecimal("2.5")),
				new PreguntaCalificable(2L, Set.of("X"), BigDecimal.ONE)
		);
	}

	@Test
	void generarResultadoCierraCalificaYPersisteConId() {
		Resultado resultado = servicio.generarResultado(10L, 20L, Map.of(1L, Set.of("A", "B"), 2L, Set.of("Y")), preguntas());

		assertNotNull(resultado.resultadoId());
		assertEquals(EstadoResultado.CALIFICADO, resultado.estado());
		assertEquals(new BigDecimal("2.5"), resultado.calificacion().orElseThrow().puntajeObtenido());
		assertEquals(new BigDecimal("3.5"), resultado.calificacion().orElseThrow().puntajeMaximo());
		assertEquals(1, resultado.calificacion().orElseThrow().respuestasCorrectas());
		assertSame(resultado, servicio.consultar(resultado.resultadoId()));
	}

	@Test
	void generarResultadoEsIdempotentePorSesionYEstudiante() {
		Resultado primero = servicio.generarResultado(10L, 20L, Map.of(1L, Set.of("A", "B")), preguntas());
		Resultado segundo = servicio.generarResultado(10L, 20L, Map.of(1L, Set.of("A")), preguntas());

		assertEquals(primero.resultadoId(), segundo.resultadoId());
		assertEquals(1, datos.size());
	}

	@Test
	void sinRespuestasTambienSeGeneraResultadoConCero() {
		Resultado resultado = servicio.generarResultado(10L, 21L, Map.of(), preguntas());

		assertEquals(0, resultado.calificacion().orElseThrow().puntajeObtenido().signum());
		assertEquals(EstadoResultado.CALIFICADO, resultado.estado());
	}

	@Test
	void cerrarYCalificarNoFallaPorCalificarDosVeces() {
		Resultado resultado = new ResultadoFactory(new GeneradorIdNumericoFake(1)).crear(1L, 2L);
		resultado.registrarRespuesta(new Respuesta(1L, Set.of("A", "B"), Instant.now()));

		servicio.cerrarYCalificar(resultado, preguntas());

		assertEquals(EstadoResultado.CALIFICADO, resultado.estado());
		assertTrue(datos.containsKey(resultado.resultadoId()));
	}

	@Test
	void consultarInexistenteLanzaNoEncontrado() {
		assertThrows(RecursoNoEncontradoException.class, () -> servicio.consultar(999L));
	}

	@Test
	void reconstruirConservaRespuestasEstadoYCalificacion() {
		Resultado original = servicio.generarResultado(10L, 22L, Map.of(1L, Set.of("A", "B")), preguntas());

		Resultado copia = Resultado.reconstruir(
				original.resultadoId(), original.sesionId(), original.estudianteId(), original.creadoEn(),
				original.estado(), original.respuestas(), original.calificacion().orElse(null)
		);

		assertEquals(EstadoResultado.CALIFICADO, copia.estado());
		assertEquals(original.calificacion(), copia.calificacion());
		assertEquals(original.respuestas(), copia.respuestas());
	}
}
