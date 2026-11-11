package com.examenLive.examenLive_api.resultados.infraestructura.entrada.web;

import com.examenLive.examenLive_api.resultados.dominio.Calificacion;
import com.examenLive.examenLive_api.resultados.dominio.Respuesta;
import com.examenLive.examenLive_api.resultados.dominio.Resultado;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;

public record ResultadoResponse(
        Long resultadoId,
        Long sesionId,
        Long estudianteId,
        Instant creadoEn,
        String estado,
        CalificacionResponse calificacion,
        List<RespuestaResponse> respuestas
) {

    public record CalificacionResponse(
            BigDecimal puntajeObtenido,
            BigDecimal puntajeMaximo,
            BigDecimal porcentaje,
            int respuestasCorrectas,
            int totalPreguntas
    ) {
        static CalificacionResponse desde(Calificacion c) {
            return new CalificacionResponse(
                    c.puntajeObtenido(), c.puntajeMaximo(), c.porcentaje(),
                    c.respuestasCorrectas(), c.totalPreguntas()
            );
        }
    }

    public record RespuestaResponse(Long preguntaId, Set<String> valoresSeleccionados, Instant enviadaEn) {
        static RespuestaResponse desde(Respuesta r) {
            return new RespuestaResponse(r.preguntaId(), r.valoresSeleccionados(), r.enviadaEn());
        }
    }

    public static ResultadoResponse desde(Resultado resultado) {
        return new ResultadoResponse(
                resultado.resultadoId(),
                resultado.sesionId(),
                resultado.estudianteId(),
                resultado.creadoEn(),
                resultado.estado().name(),
                resultado.calificacion().map(CalificacionResponse::desde).orElse(null),
                resultado.respuestas().stream().map(RespuestaResponse::desde).toList()
        );
    }
}
