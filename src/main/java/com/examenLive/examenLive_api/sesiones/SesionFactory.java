package com.examenLive.examenLive_api.sesiones;

public class SesionFactory {

    public Sesion crear(
            Long id,
            Long examenId,
            VentanaTiempo ventanaTiempo
    ) {
        if (id == null) {
            throw new IllegalArgumentException(
                    "El id de la sesión es obligatorio"
            );
        }

        if (examenId == null) {
            throw new IllegalArgumentException(
                    "El id del examen es obligatorio"
            );
        }

        if (ventanaTiempo == null) {
            throw new IllegalArgumentException(
                    "La ventana de tiempo es obligatoria"
            );
        }

        return new Sesion(id, examenId, ventanaTiempo);
    }
}