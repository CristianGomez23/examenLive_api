package com.examenLive.examenLive_api.sesiones.aplicacion.puertos.salida;

import com.examenLive.examenLive_api.sesiones.Sesion;

import java.util.Optional;

public interface SesionRepository {

    Optional<Sesion> buscarPorId(Long sesionId);

    void guardar(Sesion sesion);
}