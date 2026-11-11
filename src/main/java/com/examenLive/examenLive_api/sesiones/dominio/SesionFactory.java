package com.examenLive.examenLive_api.sesiones.dominio;

import com.examenLive.examenLive_api.identidad.dominio.puertos.salida.GeneradorId;

public class SesionFactory {

    private final GeneradorId generadorId;

    public SesionFactory(GeneradorId generadorId) {
        this.generadorId = generadorId;
    }

    public Sesion crear(Long examenId, VentanaTiempo ventanaTiempo) {
        if (examenId == null || examenId <= 0) {
            throw new IllegalArgumentException(
                    "El id del examen es obligatorio"
            );
        }

        if (ventanaTiempo == null) {
            throw new IllegalArgumentException(
                    "La ventana de tiempo es obligatoria"
            );
        }

        return new Sesion(generadorId.siguienteId(), examenId, ventanaTiempo);
    }
}