package com.examenLive.examenLive_api.sesiones.infraestructura.entrada.web;

import java.time.LocalDateTime;

public record ProgramarSesionRequest(Long examenId, LocalDateTime inicio, LocalDateTime fin) {
}