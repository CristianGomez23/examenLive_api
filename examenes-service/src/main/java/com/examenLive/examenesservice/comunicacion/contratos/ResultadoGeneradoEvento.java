package com.examenLive.examenesservice.comunicacion.contratos;


import java.time.Instant;
import java.util.UUID;
import java.math.BigDecimal;
public record ResultadoGeneradoEvento(UUID eventId, int version, Instant ocurridoEn,
        Long intentoId, Long sesionId, Long estudianteId, Long resultadoId,
        BigDecimal puntajeObtenido, BigDecimal puntajeMaximo) {
    public void validar() {
        if (eventId == null || version != 1 || ocurridoEn == null || intentoId == null || intentoId <= 0
                || resultadoId == null || resultadoId <= 0 || sesionId == null || estudianteId == null
                || puntajeObtenido == null || puntajeMaximo == null || puntajeObtenido.signum() < 0
                || puntajeMaximo.signum() <= 0 || puntajeObtenido.compareTo(puntajeMaximo) > 0)
            throw new IllegalArgumentException("Confirmación de resultado inválida.");
    }
}
