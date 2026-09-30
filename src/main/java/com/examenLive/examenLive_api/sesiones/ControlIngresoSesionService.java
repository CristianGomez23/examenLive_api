package com.examenLive.examenLive_api.sesiones;

import com.examenLive.examenLive_api.sesiones.aplicacion.ControlIngresoSesionUseCase;
import com.examenLive.examenLive_api.sesiones.aplicacion.puertos.salida.SesionRepository;

import java.time.LocalDateTime;

public class ControlIngresoSesionService implements ControlIngresoSesionUseCase {

    private final SesionRepository sesionRepository;

    public ControlIngresoSesionService() {
        this.sesionRepository = null;
    }

    public ControlIngresoSesionService(SesionRepository sesionRepository) {
        this.sesionRepository = sesionRepository;
    }

    @Override
    public boolean ingresar(
            Long sesionId,
            Long estudianteId,
            LocalDateTime momento
    ) {
        Sesion sesion = obtenerSesion(sesionId);
        if (!puedeIngresar(sesion.getVentanaTiempo(), momento)) {
            return false;
        }

        sesion.agregarEstudiante(estudianteId);
        sesionRepository.guardar(sesion);
        return true;
    }

    @Override
    public boolean reingresar(
            Long sesionId,
            Long estudianteId,
            LocalDateTime momento
    ) {
        Sesion sesion = obtenerSesion(sesionId);
        if (!puedeReingresar(sesion.getVentanaTiempo(), momento)) {
            return false;
        }

        sesion.agregarEstudiante(estudianteId);
        sesionRepository.guardar(sesion);
        return true;
    }

    private Sesion obtenerSesion(Long sesionId) {
        if (sesionRepository == null) {
            throw new IllegalStateException(
                    "El caso de uso requiere un repositorio de sesiones"
            );
        }

        return sesionRepository.buscarPorId(sesionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "La sesion no existe: " + sesionId
                ));
    }

    public boolean puedeIngresar(
            VentanaTiempo ventana,
            LocalDateTime momento
    ) {
        return ventana.estaActiva(momento);
    }

    public boolean puedeReingresar(
            VentanaTiempo ventana,
            LocalDateTime momento
    ) {
        return ventana.estaActiva(momento);
    }
}