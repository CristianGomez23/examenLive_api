package com.examenLive.examenLive_api.examenes.dominio.puertos.salida;

import com.examenLive.examenLive_api.examenes.dominio.IntentoExamen;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IntentoExamenRepository {

	IntentoExamen guardar(IntentoExamen intento);

	Optional<IntentoExamen> buscarPorId(Long intentoId);

	Optional<IntentoExamen> buscarPorSesionYEstudiante(Long sesionId, Long estudianteId);

	List<IntentoExamen> buscarVencidos(LocalDateTime ahora);
}