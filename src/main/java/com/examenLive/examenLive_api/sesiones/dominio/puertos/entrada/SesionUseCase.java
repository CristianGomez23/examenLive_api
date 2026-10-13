package com.examenLive.examenLive_api.sesiones.dominio.puertos.entrada;

import com.examenLive.examenLive_api.sesiones.dominio.Sesion;

import java.time.LocalDateTime;

public interface SesionUseCase {

	Sesion programar(Long examenId, LocalDateTime inicio, LocalDateTime fin);

	Sesion consultar(Long sesionId);
}