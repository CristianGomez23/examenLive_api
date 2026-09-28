package com.examenLive.examenLive_api.resultados.aplicacion;

import com.examenLive.examenLive_api.resultados.dominio.Resultado;
import java.util.UUID;

public interface ResultadoUseCase {
    Resultado iniciar(UUID sesionId, UUID estudianteId);
    Resultado consultar(UUID resultadoId);
}
