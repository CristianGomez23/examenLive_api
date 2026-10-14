package com.examenLive.examenLive_api.examenes.dominio.puertos.entrada;

import com.examenLive.examenLive_api.examenes.dominio.Examen;

import java.util.List;
import java.util.Set;

public interface ExamenUseCase {

	Examen crear(String titulo, List<Long> preguntasIds, long duracionMinutos);

	Examen consultar(Long examenId);

	IntentoExamenVista iniciarIntento(Long sesionId, Long estudianteId);

	IntentoExamenVista consultarIntento(Long intentoId);

	IntentoExamenVista registrarRespuesta(Long intentoId, Long preguntaId, Set<String> seleccionadas);

	IntentoExamenVista finalizarIntento(Long intentoId);
}