package com.examenLive.examenLive_api.examenes.infraestructura.entrada.web;

import java.util.List;

public record CrearExamenRequest(String titulo, List<Long> preguntasIds, Long duracionMinutos) {
}