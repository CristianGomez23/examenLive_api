package com.examenLive.examenLive_api.resultados;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResultadoFactoryTest {

	private final ResultadoFactory resultadoFactory = new ResultadoFactory();

	@Test
	void creaLaRaizDelAgregadoCompleta() {
		UUID sesionId = UUID.randomUUID();
		UUID estudianteId = UUID.randomUUID();

		Resultado resultado = resultadoFactory.crear(sesionId, estudianteId);

		assertThat(resultado.resultadoId()).isNotNull();
		assertThat(resultado.sesionId()).isEqualTo(sesionId);
		assertThat(resultado.estudianteId()).isEqualTo(estudianteId);
		assertThat(resultado.creadoEn()).isNotNull();
		assertThat(resultado.estado()).isEqualTo(EstadoResultado.EN_CURSO);
	}

	@Test
	void rechazaLaCreacionSinSesion() {
		assertThatThrownBy(() -> resultadoFactory.crear(null, UUID.randomUUID()))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("sesión");
	}
}
