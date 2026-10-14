package com.examenLive.examenLive_api.resultados;

import com.examenLive.examenLive_api.resultados.aplicacion.servicios.CalificacionAutomaticaService;
import com.examenLive.examenLive_api.resultados.aplicacion.ResultadoFactory;
import com.examenLive.examenLive_api.resultados.dominio.*;
import com.examenLive.examenLive_api.identidad.GeneradorIdNumericoFake;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CalificacionAutomaticaServiceTest {

	private final CalificacionAutomaticaService calificacionService =
			new CalificacionAutomaticaService();

	@Test
	void rechazaLaCalificacionSiHayPreguntasSinPuntaje() {
		Resultado resultado = resultadoCerrado();
		PreguntaCalificable pregunta = new PreguntaCalificable(
				10L, Set.of("A"), null);

		assertThatThrownBy(() -> calificacionService.calificar(resultado, List.of(pregunta)))
				.isInstanceOf(PreguntaSinPuntajeException.class)
				.hasMessageContaining("sin puntaje");
		assertThat(resultado.estado()).isEqualTo(EstadoResultado.CERRADO);
		assertThat(resultado.calificacion()).isEmpty();
	}

	@Test
	void calculaYRegistraLaCalificacionAutomatica() {
		Long preguntaCorrectaId = 10L;
		Long preguntaIncorrectaId = 11L;
		Resultado resultado = new ResultadoFactory(new GeneradorIdNumericoFake(1)).crear(20L, 30L);
		resultado.registrarRespuesta(new Respuesta(preguntaCorrectaId, Set.of("A"), Instant.now()));
		resultado.registrarRespuesta(new Respuesta(preguntaIncorrectaId, Set.of("B"), Instant.now()));
		resultado.cerrarRecepcionRespuestas();
		List<PreguntaCalificable> preguntas = List.of(
				new PreguntaCalificable(preguntaCorrectaId, Set.of("A"), BigDecimal.valueOf(2)),
				new PreguntaCalificable(preguntaIncorrectaId, Set.of("C"), BigDecimal.valueOf(3))
		);

		Calificacion calificacion = calificacionService.calificar(resultado, preguntas);

		assertThat(calificacion.puntajeObtenido()).isEqualByComparingTo("2");
		assertThat(calificacion.puntajeMaximo()).isEqualByComparingTo("5");
		assertThat(calificacion.respuestasCorrectas()).isEqualTo(1);
		assertThat(resultado.estado()).isEqualTo(EstadoResultado.CALIFICADO);
	}

	private Resultado resultadoCerrado() {
		Resultado resultado = new ResultadoFactory(new GeneradorIdNumericoFake(1)).crear(20L, 30L);
		resultado.cerrarRecepcionRespuestas();
		return resultado;
	}
}
