package com.examenLive.examenLive_api.sesiones;

import java.util.HashSet;
import java.util.Set;

public class Sesion {

    private final Long id;
    private final Long examenId;
    private final VentanaTiempo ventanaTiempo;
    private final Set<Long> estudiantesIds;

    public Sesion(
        Long id,
        Long examenId,
        VentanaTiempo ventanaTiempo
) {
    this.id = id;
    this.examenId = examenId;
    this.ventanaTiempo = ventanaTiempo;
    this.estudiantesIds = new HashSet<>();
}

    public void agregarEstudiante(Long estudianteId) {
        if (estudianteId == null) {
            throw new IllegalArgumentException(
                    "El id del estudiante es obligatorio"
            );
        }

        estudiantesIds.add(estudianteId);
    }

    public Long getId() {
        return id;
    }

    public Long getExamenId() {
        return examenId;
    }

    public VentanaTiempo getVentanaTiempo() {
        return ventanaTiempo;
    }

    public Set<Long> getEstudiantesIds() {
        return Set.copyOf(estudiantesIds);
    }
}