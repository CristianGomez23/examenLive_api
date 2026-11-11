package com.examenLive.sesionesservice.sesiones.infraestructura.entrada.web;

import java.time.LocalDateTime;

public record ProgramarSesionRequest(Long examenId, LocalDateTime inicio, LocalDateTime fin) {
}
