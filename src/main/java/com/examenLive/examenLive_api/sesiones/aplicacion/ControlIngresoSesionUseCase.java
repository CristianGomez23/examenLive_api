package com.examenLive.examenLive_api.sesiones.aplicacion;

import java.time.LocalDateTime;

public interface ControlIngresoSesionUseCase {

    boolean ingresar(Long sesionId, Long estudianteId, LocalDateTime momento);

    boolean reingresar(Long sesionId, Long estudianteId, LocalDateTime momento);
}