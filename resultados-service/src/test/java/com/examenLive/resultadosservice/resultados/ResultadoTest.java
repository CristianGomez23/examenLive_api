package com.examenLive.resultadosservice.resultados;

import com.examenLive.resultadosservice.resultados.aplicacion.ResultadoFactory;
import com.examenLive.resultadosservice.resultados.dominio.EstadoResultado;
import com.examenLive.resultadosservice.resultados.dominio.Respuesta;
import com.examenLive.resultadosservice.resultados.dominio.RespuestaNoModificableException;
import com.examenLive.resultadosservice.resultados.dominio.Resultado;
import com.examenLive.resultadosservice.resultados.GeneradorIdNumericoFake;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResultadoTest {

	@Test
	void reemplazaLaRespuestaMientrasLaSesionSigueAbierta() {
		Resultado resultado = nuevoResultado();
		Long preguntaId = 10L;
		resultado.registrarRespuesta(new Respuesta(preguntaId, Set.of("A"), Instant.now()));

		resultado.registrarRespuesta(new Respuesta(preguntaId, Set.of("B"), Instant.now()));

		assertThat(resultado.respuestas()).hasSize(1);
		assertThat(resultado.respuestaPara(preguntaId).orElseThrow().valoresSeleccionados())
				.containsExactly("B");
	}

	@Test
	void rechazaCambiosEnLasRespuestasDespuesDelCierre() {
		Resultado resultado = nuevoResultado();
		resultado.cerrarRecepcionRespuestas();

		assertThatThrownBy(() -> resultado.registrarRespuesta(
				new Respuesta(11L, Set.of("A"), Instant.now())))
				.isInstanceOf(RespuestaNoModificableException.class)
				.hasMessageContaining("cierre");
	}

	@Test
	void cambiaAEstadoCerradoAlFinalizarLaRecepcion() {
		Resultado resultado = nuevoResultado();

		resultado.cerrarRecepcionRespuestas();

		assertThat(resultado.estado()).isEqualTo(EstadoResultado.CERRADO);
	}

	private Resultado nuevoResultado() {
		return new ResultadoFactory(new GeneradorIdNumericoFake(1)).crear(20L, 30L);
	}
}
