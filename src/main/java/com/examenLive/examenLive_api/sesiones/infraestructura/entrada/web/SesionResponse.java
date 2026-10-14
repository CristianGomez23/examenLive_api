package com.examenLive.examenLive_api.sesiones.infraestructura.entrada.web;

import com.examenLive.examenLive_api.sesiones.dominio.Sesion;

import java.time.LocalDateTime;
import java.util.Set;

public record SesionResponse(
		Long sesionId,
		Long examenId,
		LocalDateTime inicio,
		LocalDateTime fin,
		Set<Long> estudiantesIds
) {

	public static SesionResponse desde(Sesion sesion) {
		return new SesionResponse(
				sesion.getId(), sesion.getExamenId(),
				sesion.getVentanaTiempo().inicio(), sesion.getVentanaTiempo().fin(),
				sesion.getEstudiantesIds()
		);
	}
}