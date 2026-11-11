package com.examenLive.resultadosservice.comunicacion.contratos;


import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
public record DatosIntento(Long intentoId, Long sesionId, Long estudianteId,
                           List<Respuesta> respuestas, List<Pregunta> preguntas) {
    public record Respuesta(Long preguntaId, Set<String> opcionesSeleccionadas) {}
    public record Pregunta(Long preguntaId, Set<String> respuestasCorrectas, BigDecimal puntaje) {}
    public void validar() {
        if (intentoId == null || intentoId <= 0 || sesionId == null || sesionId <= 0
                || estudianteId == null || estudianteId <= 0)
            throw new IllegalArgumentException("Los identificadores deben ser positivos.");
        if (respuestas == null || preguntas == null || preguntas.isEmpty())
            throw new IllegalArgumentException("El intento requiere respuestas y preguntas.");
    }
}
