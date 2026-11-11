package com.examenLive.sesionesservice.sesiones.dominio.puertos.salida;

import com.examenLive.sesionesservice.sesiones.dominio.Sesion;

import java.util.Optional;

public interface SesionRepository {

    Optional<Sesion> buscarPorId(Long sesionId);

    void guardar(Sesion sesion);
}
