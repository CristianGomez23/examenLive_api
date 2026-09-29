package com.examenLive.examenLive_api.resultados;

import com.examenLive.examenLive_api.resultados.aplicacion.CalificacionAutomaticaService;
import com.examenLive.examenLive_api.resultados.aplicacion.ResultadoFactory;
import com.examenLive.examenLive_api.resultados.dominio.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CalificacionAutomaticaServiceTest {

	private final CalificacionAutomaticaService calificacionService =
			new CalificacionAutomaticaService();

	@Test
	void rechazaLaCalificacionSiHayPreguntasSinPuntaje() {
		Resultado resultado = resultadoCerrado();
		PreguntaCalificable pregunta = new PreguntaCalificable(
				UUID.randomUUID(), Set.of("A"), null);

		assertThatThrownBy(() -> calificacionService.calificar(resultado, List.of(pregunta)))
				.isInstanceOf(PreguntaSinPuntajeException.class)
				.hasMessageContaining("sin puntaje");
		assertThat(resultado.estado()).isEqualTo(EstadoResultado.CERRADO);
		assertThat(resultado.calificacion()).isEmpty();
	}

	@Test
	void calculaYRegistraLaCalificacionAutomatica() {
		UUID preguntaCorrectaId = UUID.randomUUID();
		UUID preguntaIncorrectaId = UUID.randomUUID();
		Resultado resultado = new ResultadoFactory().crear(UUID.randomUUID(), UUID.randomUUID());
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
		Resultado resultado = new ResultadoFactory().crear(UUID.randomUUID(), UUID.randomUUID());
		resultado.cerrarRecepcionRespuestas();
		return resultado;
	}
}
