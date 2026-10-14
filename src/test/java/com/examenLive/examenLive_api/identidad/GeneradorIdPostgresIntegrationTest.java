package com.examenLive.examenLive_api.identidad;

import com.examenLive.examenLive_api.identidad.dominio.puertos.salida.GeneradorId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class GeneradorIdPostgresIntegrationTest {

	@Autowired
	private GeneradorId generadorId;

	@Test
	void generaIdsNumericosGlobalesCrecientesEnPostgres() {
		long primero = generadorId.siguienteId();
		long segundo = generadorId.siguienteId();

		assertTrue(primero > 0);
		assertTrue(segundo > primero);
	}
}