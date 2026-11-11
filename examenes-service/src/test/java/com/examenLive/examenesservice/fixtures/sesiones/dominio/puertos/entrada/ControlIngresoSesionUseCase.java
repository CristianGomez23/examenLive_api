package com.examenLive.examenesservice.fixtures.sesiones.dominio.puertos.entrada;

import java.time.LocalDateTime;

public interface ControlIngresoSesionUseCase {

    boolean ingresar(Long sesionId, Long estudianteId, LocalDateTime momento);

    boolean reingresar(Long sesionId, Long estudianteId, LocalDateTime momento);
}
