package com.examenLive.resultadosservice.resultados.dominio.puertos.salida;

import com.examenLive.resultadosservice.resultados.dominio.Resultado;

import java.util.List;
import java.util.Optional;

public interface RepositorioResultados {
    Resultado guardar(Resultado resultado);
    Optional<Resultado> buscarPorId(Long resultadoId);
    Optional<Resultado> buscarPorSesionYEstudiante(Long sesionId, Long estudianteId);
    List<Resultado> buscarPorEstudiante(Long estudianteId);
}
