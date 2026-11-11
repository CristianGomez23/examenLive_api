package com.examenLive.resultadosservice.resultados;

import com.examenLive.resultadosservice.resultados.aplicacion.ResultadoFactory;
import com.examenLive.resultadosservice.resultados.dominio.EstadoResultado;
import com.examenLive.resultadosservice.resultados.dominio.Resultado;
import com.examenLive.resultadosservice.resultados.GeneradorIdNumericoFake;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResultadoFactoryTest {

	private final ResultadoFactory resultadoFactory = new ResultadoFactory(new GeneradorIdNumericoFake(1));

	@Test
	void creaLaRaizDelAgregadoCompleta() {
		Long sesionId = 20L;
		Long estudianteId = 30L;

		Resultado resultado = resultadoFactory.crear(sesionId, estudianteId);

		assertThat(resultado.resultadoId()).isNotNull();
		assertThat(resultado.sesionId()).isEqualTo(sesionId);
		assertThat(resultado.estudianteId()).isEqualTo(estudianteId);
		assertThat(resultado.creadoEn()).isNotNull();
		assertThat(resultado.estado()).isEqualTo(EstadoResultado.EN_CURSO);
	}

	@Test
	void rechazaLaCreacionSinSesion() {
		assertThatThrownBy(() -> resultadoFactory.crear(null, 30L))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("sesión");
	}
}
