package com.examenLive.examenLive_api.sesiones;

import java.time.LocalDateTime;

public class ControlIngresoSesionService {

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