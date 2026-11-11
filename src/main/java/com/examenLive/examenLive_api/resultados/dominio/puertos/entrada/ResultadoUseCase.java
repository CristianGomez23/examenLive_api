package com.examenLive.examenLive_api.resultados.dominio.puertos.entrada;

import com.examenLive.examenLive_api.resultados.dominio.PreguntaCalificable;
import com.examenLive.examenLive_api.resultados.dominio.Resultado;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface ResultadoUseCase {
    Resultado iniciar(Long sesionId, Long estudianteId);

    Resultado consultar(Long resultadoId);

    /**
     * Genera (una sola vez por sesión y estudiante) el resultado calificado de un intento terminado.
     * Si ya existe, devuelve el existente.
     */
    Resultado generarResultado(
            Long sesionId,
            Long estudianteId,
            Map<Long, Set<String>> respuestas,
            List<PreguntaCalificable> preguntas
    );

    Optional<Resultado> buscarPorSesionYEstudiante(Long sesionId, Long estudianteId);

    List<Resultado> consultarPorEstudiante(Long estudianteId);
}
