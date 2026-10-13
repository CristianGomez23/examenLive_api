package com.examenLive.examenLive_api.resultados.dominio.puertos.entrada;

import com.examenLive.examenLive_api.resultados.dominio.Resultado;
public interface ResultadoUseCase {
    Resultado iniciar(Long sesionId, Long estudianteId);
    Resultado consultar(Long resultadoId);
}
