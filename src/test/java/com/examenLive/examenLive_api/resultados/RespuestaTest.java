package com.examenLive.examenLive_api.resultados;

import com.examenLive.examenLive_api.resultados.dominio.Respuesta;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RespuestaTest {

	@Test
	void creaUnaRespuestaInmutable() {
		Set<String> seleccion = new LinkedHashSet<>(Set.of("A"));
		Respuesta respuesta = new Respuesta(UUID.randomUUID(), seleccion, Instant.now());

		seleccion.add("B");

		assertThat(respuesta.valoresSeleccionados()).containsExactly("A");
		assertThatThrownBy(() -> respuesta.valoresSeleccionados().add("C"))
				.isInstanceOf(UnsupportedOperationException.class);
	}

	@Test
	void rechazaUnaRespuestaSinPregunta() {
		assertThatThrownBy(() -> new Respuesta(null, Set.of("A"), Instant.now()))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("pregunta");
	}

	@Test
	void rechazaUnaRespuestaSinValoresSeleccionados() {
		assertThatThrownBy(() -> new Respuesta(UUID.randomUUID(), Set.of(), Instant.now()))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("valor");
	}
}
