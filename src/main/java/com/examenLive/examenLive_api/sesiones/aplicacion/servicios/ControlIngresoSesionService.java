package com.examenLive.examenLive_api.sesiones.aplicacion.servicios;

import com.examenLive.examenLive_api.sesiones.dominio.puertos.entrada.ControlIngresoSesionUseCase;
import com.examenLive.examenLive_api.sesiones.dominio.puertos.salida.SesionRepository;
import com.examenLive.examenLive_api.sesiones.dominio.Sesion;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ControlIngresoSesionService implements ControlIngresoSesionUseCase {

    private final SesionRepository sesionRepository;

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
        if (!sesion.getVentanaTiempo().estaActiva(momento)) {
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
        if (!sesion.getVentanaTiempo().estaActiva(momento)) {
            return false;
        }

        sesion.agregarEstudiante(estudianteId);
        sesionRepository.guardar(sesion);
        return true;
    }

    private Sesion obtenerSesion(Long sesionId) {
        return sesionRepository.buscarPorId(sesionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "La sesion no existe: " + sesionId
                ));
    }

}