package com.examenLive.resultadosservice.resultados.dominio.puertos.salida;


import java.util.Optional;
public interface VinculosIntentos {
    void bloquear(Long intentoId);
    Optional<Long> buscarResultado(Long intentoId);
    void guardar(Long intentoId, Long resultadoId, Long sesionId, Long estudianteId);
}
