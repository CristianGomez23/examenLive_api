package com.examenLive.examenLive_api.resultados.aplicacion;

import com.examenLive.examenLive_api.resultados.dominio.Resultado;
import java.util.Optional;
import java.util.UUID;

public interface RepositorioResultados {
    Resultado guardar(Resultado resultado);
    Optional<Resultado> buscarPorId(UUID resultadoId);
}
