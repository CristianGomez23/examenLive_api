package com.examenLive.examenLive_api.resultados.aplicacion;

import com.examenLive.examenLive_api.resultados.dominio.Resultado;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

class ResultadoHexagonalFakeTest {

 @Test
 void iniciaResultadoSinSpringNiBaseDatos(){
    RepositorioResultadosFake fake = new RepositorioResultadosFake();
    ResultadoService service = new ResultadoService(
       fake,
       new ResultadoFactory(),
       null
    );

    Resultado resultado = service.iniciar(UUID.randomUUID(), UUID.randomUUID());

    assertThat(resultado.resultadoId()).isNotNull();
    assertThat(fake.buscarPorId(resultado.resultadoId())).isPresent();
 }
}
