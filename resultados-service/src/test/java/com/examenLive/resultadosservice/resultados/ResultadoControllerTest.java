package com.examenLive.resultadosservice.resultados;

import com.examenLive.resultadosservice.compartido.RecursoNoEncontradoException;
import com.examenLive.resultadosservice.resultados.aplicacion.ResultadoFactory;
import com.examenLive.resultadosservice.resultados.aplicacion.RepositorioResultadosFake;
import com.examenLive.resultadosservice.resultados.aplicacion.servicios.CalificacionAutomaticaService;
import com.examenLive.resultadosservice.resultados.aplicacion.servicios.ResultadoService;
import com.examenLive.resultadosservice.resultados.dominio.PreguntaSinPuntajeException;
import com.examenLive.resultadosservice.resultados.infraestructura.entrada.web.GenerarResultadoRequest;
import com.examenLive.resultadosservice.resultados.infraestructura.entrada.web.GenerarResultadoRequest.PreguntaRequest;
import com.examenLive.resultadosservice.resultados.infraestructura.entrada.web.GenerarResultadoRequest.RespuestaRequest;
import com.examenLive.resultadosservice.resultados.infraestructura.entrada.web.ResultadoController;
import com.examenLive.resultadosservice.resultados.infraestructura.entrada.web.ResultadoResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba el contrato HTTP del servicio (sin levantar Spring): estados, idempotencia y validaciones.
 */
class ResultadoControllerTest {

	private final ResultadoController controlador = new ResultadoController(new ResultadoService(
			new RepositorioResultadosFake(),
			new ResultadoFactory(new GeneradorIdNumericoFake(1000)),
			new CalificacionAutomaticaService()
	), true);

	private static List<PreguntaRequest> preguntas() {
		return List.of(
				new PreguntaRequest(101L, Set.of("Java", "Kotlin"), new BigDecimal("2.5")),
				new PreguntaRequest(102L, Set.of("Spring Boot"), BigDecimal.ONE)
		);
	}

	private static GenerarResultadoRequest solicitud(Long sesion, Long estudiante, List<RespuestaRequest> respuestas, List<PreguntaRequest> preguntas) {
		return new GenerarResultadoRequest(sesion, estudiante, respuestas, preguntas);
	}

	@Test
	void creaConEstado201YLuegoEsIdempotenteConEstado200() {
		var respuestas = List.of(
				new RespuestaRequest(101L, Set.of("Java", "Kotlin")),
				new RespuestaRequest(102L, Set.of("Django"))
		);

		ResponseEntity<ResultadoResponse> primera = controlador.generar(solicitud(1L, 700L, respuestas, preguntas()));
		ResponseEntity<ResultadoResponse> segunda = controlador.generar(solicitud(1L, 700L, respuestas, preguntas()));

		assertEquals(201, primera.getStatusCode().value());
		assertEquals(200, segunda.getStatusCode().value());
		assertEquals(primera.getBody().resultadoId(), segunda.getBody().resultadoId());
		assertEquals("CALIFICADO", primera.getBody().estado());
		assertEquals(0, new BigDecimal("2.5").compareTo(primera.getBody().calificacion().puntajeObtenido()));
		assertEquals(0, new BigDecimal("3.5").compareTo(primera.getBody().calificacion().puntajeMaximo()));
		assertEquals(0, new BigDecimal("71.43").compareTo(primera.getBody().calificacion().porcentaje()));
		assertEquals(1, primera.getBody().calificacion().respuestasCorrectas());
		assertEquals(2, primera.getBody().calificacion().totalPreguntas());
	}

	@Test
	void consultaPorIdYPorEstudiante() {
		Long id = controlador.generar(solicitud(2L, 701L, List.of(new RespuestaRequest(102L, Set.of("Spring Boot"))), preguntas()))
				.getBody().resultadoId();

		assertEquals(id, controlador.consultar(id).resultadoId());
		assertEquals(1, controlador.consultarPorEstudiante(701L, null).size());
		assertEquals(1, controlador.consultarPorEstudiante(701L, 2L).size());
		assertEquals(0, controlador.consultarPorEstudiante(701L, 99L).size());
		assertThrows(RecursoNoEncontradoException.class, () -> controlador.consultar(999999L));
	}

	@Test
	void sinRespuestasObtieneCeroPuntos() {
		var cuerpo = controlador.generar(solicitud(3L, 702L, null, preguntas())).getBody();

		assertEquals(0, cuerpo.calificacion().puntajeObtenido().signum());
		assertEquals(0, cuerpo.respuestas().size());
	}

	@Test
	void seleccionParcialNoCuentaComoCorrecta() {
		var cuerpo = controlador.generar(solicitud(4L, 703L,
				List.of(new RespuestaRequest(101L, Set.of("Java"))), preguntas())).getBody();

		assertEquals(0, cuerpo.calificacion().respuestasCorrectas());
	}

	@Test
	void rechazaSolicitudesInvalidas() {
		var ok = List.of(new RespuestaRequest(101L, Set.of("Java")));
		assertThrows(IllegalArgumentException.class, () -> controlador.generar(null));
		assertThrows(IllegalArgumentException.class, () -> controlador.generar(solicitud(null, 1L, ok, preguntas())));
		assertThrows(IllegalArgumentException.class, () -> controlador.generar(solicitud(1L, null, ok, preguntas())));
		assertThrows(IllegalArgumentException.class, () -> controlador.generar(solicitud(5L, 704L, ok, List.of())));
		assertThrows(IllegalArgumentException.class, () -> controlador.generar(solicitud(5L, 704L, ok, null)));
		// respuesta a una pregunta que no es del examen
		assertThrows(IllegalArgumentException.class, () -> controlador.generar(
				solicitud(5L, 704L, List.of(new RespuestaRequest(999L, Set.of("X"))), preguntas())));
		// pregunta duplicada en la solicitud
		assertThrows(IllegalArgumentException.class, () -> controlador.generar(
				solicitud(5L, 704L, ok, List.of(preguntas().get(0), preguntas().get(0)))));
		// dos respuestas para la misma pregunta
		assertThrows(IllegalArgumentException.class, () -> controlador.generar(
				solicitud(5L, 704L, List.of(new RespuestaRequest(101L, Set.of("Java")), new RespuestaRequest(101L, Set.of("Kotlin"))), preguntas())));
		// respuesta sin opciones
		assertThrows(IllegalArgumentException.class, () -> controlador.generar(
				solicitud(5L, 704L, List.of(new RespuestaRequest(101L, Set.of())), preguntas())));
	}

	@Test
	void noCalificaSiUnaPreguntaNoTienePuntaje() {
		var sinPuntaje = List.of(new PreguntaRequest(101L, Set.of("Java"), null));

		assertThrows(PreguntaSinPuntajeException.class, () -> controlador.generar(
				solicitud(6L, 705L, List.of(new RespuestaRequest(101L, Set.of("Java"))), sinPuntaje)));
	}
}
