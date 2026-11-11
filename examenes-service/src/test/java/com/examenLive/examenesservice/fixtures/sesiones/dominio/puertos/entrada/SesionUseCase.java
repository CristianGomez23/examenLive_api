package com.examenLive.examenesservice.fixtures.sesiones.dominio.puertos.entrada;

import com.examenLive.examenesservice.fixtures.sesiones.dominio.Sesion;

import java.time.LocalDateTime;

public interface SesionUseCase {

	Sesion programar(Long examenId, LocalDateTime inicio, LocalDateTime fin);

	Sesion consultar(Long sesionId);
}
