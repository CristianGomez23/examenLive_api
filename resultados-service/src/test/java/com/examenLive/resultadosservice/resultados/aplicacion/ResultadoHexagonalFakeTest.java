package com.examenLive.resultadosservice.resultados.aplicacion;

import com.examenLive.resultadosservice.resultados.dominio.Resultado;
import com.examenLive.resultadosservice.resultados.GeneradorIdNumericoFake;
import com.examenLive.resultadosservice.resultados.aplicacion.servicios.ResultadoService;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ResultadoHexagonalFakeTest {

 @Test
 void iniciaResultadoSinSpringNiBaseDatos(){
    RepositorioResultadosFake fake = new RepositorioResultadosFake();
    ResultadoService service = new ResultadoService(
       fake,
      new ResultadoFactory(new GeneradorIdNumericoFake(1)),
       null
    );

   Resultado resultado = service.iniciar(20L, 30L);

    assertThat(resultado.resultadoId()).isNotNull();
    assertThat(fake.buscarPorId(resultado.resultadoId())).isPresent();
 }
}
