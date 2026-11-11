package com.examenLive.examenLive_api.sesiones.infraestructura.persistencia;

import com.examenLive.examenLive_api.sesiones.Sesion;

import java.util.Optional;

public interface SesionDataRepository {

    Optional<Sesion> findById(Long sesionId);

    Sesion save(Sesion sesion);
}