package com.examenLive.resultadosservice.resultados;

import com.examenLive.resultadosservice.resultados.aplicacion.servicios.CalificacionAutomaticaService;
import com.examenLive.resultadosservice.resultados.dominio.puertos.salida.RepositorioResultados;
import com.examenLive.resultadosservice.resultados.aplicacion.ResultadoFactory;
import com.examenLive.resultadosservice.resultados.aplicacion.servicios.ResultadoService;
import com.examenLive.resultadosservice.resultados.dominio.Resultado;
import com.examenLive.resultadosservice.resultados.GeneradorIdNumericoFake;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResultadoServiceTest {

	@Mock
	private ResultadoFactory resultadoFactory;

	@Mock
	private CalificacionAutomaticaService calificacionAutomaticaService;

	@Mock
	private RepositorioResultados repositorioResultados;

	@InjectMocks
	private ResultadoService resultadoService;

	@Test
	void iniciarDelegaLaCreacionEnLaFactory() {
		Long sesionId = 20L;
		Long estudianteId = 30L;

		Resultado esperado = new ResultadoFactory(new GeneradorIdNumericoFake(1)).crear(sesionId, estudianteId);

		when(resultadoFactory.crear(sesionId, estudianteId)).thenReturn(esperado);

		when(repositorioResultados.guardar(esperado)).thenReturn(esperado);

		Resultado resultado = resultadoService.iniciar(sesionId, estudianteId);

		assertThat(resultado).isSameAs(esperado);

		verify(resultadoFactory).crear(sesionId, estudianteId);
		verify(repositorioResultados).guardar(esperado);
	}
}
