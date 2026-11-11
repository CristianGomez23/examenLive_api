package com.examenLive.examenLive_api.examenes.infraestructura.entrada.web;

import java.util.Set;

public record RegistrarRespuestaRequest(Set<String> opcionesSeleccionadas) {
}