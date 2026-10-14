package com.examenLive.examenLive_api.sesiones.dominio.puertos.salida;

import com.examenLive.examenLive_api.sesiones.dominio.Sesion;

import java.util.Optional;

public interface SesionRepository {

    Optional<Sesion> buscarPorId(Long sesionId);

    void guardar(Sesion sesion);
}