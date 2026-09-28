package com.examenLive.examenLive_api.resultados;

import com.examenLive.examenLive_api.resultados.aplicacion.CalificacionAutomaticaService;
import com.examenLive.examenLive_api.resultados.aplicacion.RepositorioResultados;
import com.examenLive.examenLive_api.resultados.aplicacion.ResultadoFactory;
import com.examenLive.examenLive_api.resultados.aplicacion.ResultadoService;
import com.examenLive.examenLive_api.resultados.dominio.Resultado;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

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
		UUID sesionId = UUID.randomUUID();
		UUID estudianteId = UUID.randomUUID();

		Resultado esperado = new ResultadoFactory().crear(sesionId, estudianteId);

		when(resultadoFactory.crear(sesionId, estudianteId)).thenReturn(esperado);

		when(repositorioResultados.guardar(esperado)).thenReturn(esperado);

		Resultado resultado = resultadoService.iniciar(sesionId, estudianteId);

		assertThat(resultado).isSameAs(esperado);

		verify(resultadoFactory).crear(sesionId, estudianteId);
		verify(repositorioResultados).guardar(esperado);
	}
}
