package com.examenLive.resultadosservice.comunicacion.contratos;


import java.time.Instant;
import java.util.UUID;
public record IntentoFinalizadoEvento(UUID eventId, int version, Instant ocurridoEn, DatosIntento intento) {
    public void validar() {
        if (eventId == null || version != 1 || ocurridoEn == null || intento == null)
            throw new IllegalArgumentException("Evento de intento inválido o versión no soportada.");
        intento.validar();
    }
}
