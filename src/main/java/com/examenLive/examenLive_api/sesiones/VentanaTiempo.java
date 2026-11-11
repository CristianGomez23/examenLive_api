package com.examenLive.examenLive_api.sesiones;

import java.time.LocalDateTime;

public record VentanaTiempo(
        LocalDateTime inicio,
        LocalDateTime fin
) {

    public VentanaTiempo {
        if (inicio == null) {
            throw new IllegalArgumentException("El inicio de la ventana es obligatorio");
        }

        if (fin == null) {
            throw new IllegalArgumentException("El fin de la ventana es obligatorio");
        }

        if (!inicio.isBefore(fin)) {
            throw new IllegalArgumentException(
                    "El inicio debe ser anterior al fin de la ventana"
            );
        }
    }

    public boolean estaActiva(LocalDateTime momento) {
        return !momento.isBefore(inicio) && momento.isBefore(fin);
    }
}